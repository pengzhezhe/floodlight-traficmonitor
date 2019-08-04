package net.floodlightcontroller.trafficmonitor;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.statistics.IStatisticsService;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.floodlightcontroller.trafficmonitor.web.TrafficMonitorWebRoutable;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.U64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class TrafficMonitor implements IFloodlightModule,ITrafficMonitorService {
    protected IFloodlightProviderService floodlightProvider;        //注册
    protected static Logger logger=LoggerFactory.getLogger(TrafficMonitor.class);  //日志信息打印出所在类
    //获取所有交换机信息
    private static IOFSwitchService switchService;
    protected static IStatisticsService statisticsService;  //调用自带的数据模块对其改进后使用
    //floodlight实现的线程池
    private static IThreadPoolService threadPoolService;

    private static ScheduledFuture<?> portStatCollector;
    //存放每条路的带宽使用情况
    private static HashMap<NodePortTuple, SwitchPortBandwidth> bandwidth=new HashMap<NodePortTuple,SwitchPortBandwidth>();
    //异常记录日志
    private static HashMap<NodePortTuple, SwitchPortBandwidth> exceptionLog=new HashMap<NodePortTuple,SwitchPortBandwidth>();

    //搜集数据的周期
    private static final int portStatInterval = 10;

    private IRestApiService restApiService;
    //策略
    private static Strategy strategy=new Strategy();
    //事件
    private static LinkedList<Event> events=new LinkedList<Event>();
    // 流表项添加记录，防止下发重复流表项
    public static HashMap<NodePortTuple, Date> addFlowEntriesHistory = new HashMap<NodePortTuple, Date>();

    private ILinkDiscoveryService linkDiscoveryService;
    //白名单
    private static LinkedList<WhiteList> whiteLists=new LinkedList<WhiteList>();

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l=new ArrayList<Class<? extends IFloodlightService>>();
        l.add(ITrafficMonitorService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
       Map<Class<? extends IFloodlightService>,IFloodlightService> m=new HashMap<Class<? extends IFloodlightService>,IFloodlightService>();
        m.put(ITrafficMonitorService.class, this);
       return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
        l.add(IStatisticsService.class);
        l.add(IOFSwitchService.class);
        l.add(IThreadPoolService.class);
        l.add(IRestApiService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
        statisticsService=context.getServiceImpl(IStatisticsService.class);
        switchService=context.getServiceImpl(IOFSwitchService.class);
        threadPoolService=context.getServiceImpl(IThreadPoolService.class);
        restApiService = context.getServiceImpl(IRestApiService.class);
        linkDiscoveryService = context.getServiceImpl(ILinkDiscoveryService.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context)throws FloodlightModuleException {
        /* 设置请求url */
        restApiService.addRestletRoutable(new TrafficMonitorWebRoutable());
        startCollectBandwidth(switchService,threadPoolService,strategy,events);
    }

    //收集数据的方法
    private synchronized void startCollectBandwidth(IOFSwitchService switchService, IThreadPoolService threadPoolService,Strategy strategy,LinkedList<Event> events){
        portStatCollector=threadPoolService.getScheduledExecutor().scheduleAtFixedRate(new GetBandwidthThread(),portStatInterval,portStatInterval, TimeUnit.SECONDS);
        logger.warn("TrafficMonitor thread start");
    }

    //自定义线程类
    private class GetBandwidthThread extends Thread implements Runnable{
        private Map<NodePortTuple,SwitchPortBandwidth> bandwidth;

        public Map<NodePortTuple,SwitchPortBandwidth> getBandwidth(){
            return bandwidth;
        }
        @Override
        public void run(){
            System.out.println("Monitor Thread run");
            bandwidth=getBandwidthMap();
            //System.out.println("bandwidth:"+bandwidth.size());
        }
    }

    //获取带宽信息
    public Map<NodePortTuple,SwitchPortBandwidth> getBandwidthMap(){
        bandwidth.putAll(statisticsService.getBandwidthConsumption());
        Iterator<Map.Entry<NodePortTuple,SwitchPortBandwidth>> iter=bandwidth.entrySet().iterator();
        while (iter.hasNext()){
            HashMap.Entry<NodePortTuple,SwitchPortBandwidth> entry=iter.next();
            NodePortTuple tuple=entry.getKey();
            SwitchPortBandwidth switchPortBand=entry.getValue();
            if(tuple.getPortId().getPortNumber()<0) {
                iter.remove();
                continue;
            }
            System.out.print(tuple.getNodeId() + "," + tuple.getPortId().getPortNumber() + ",");
            System.out.println("in:"+switchPortBand.getBitsPerSecondRx().getValue()/8 +"b/s,out:"+switchPortBand.getBitsPerSecondTx().getValue() / 8+"b/s");
        }
        exceptionLog.clear();
        TrafficAnalyzer.Analysis(bandwidth,exceptionLog,strategy);
        if(!exceptionLog.isEmpty()){
            TrafficController.execute(switchService,exceptionLog,strategy,events,addFlowEntriesHistory,linkDiscoveryService);
        }
        return bandwidth;
    }

    public SwitchPortBandwidth getBandwidth(DatapathId dpid, OFPort port){
        SwitchPortBandwidth bw=statisticsService.getBandwidthConsumption(dpid,port);
        System.out.print(bw.getBitsPerSecondRx().getValue()/8);
        return bw;
    }

    public Strategy getStrategy(){
        return strategy;
    }

    public LinkedList<Event> getEvents(){
        return events;
    }

    public void setStrategy(U64 maxSpeed,String action,long actionDuring){
        strategy.setMaxSpeed(maxSpeed);
        strategy.setAction(action);
        strategy.setActionDuration(actionDuring);
    }

    public LinkedList<WhiteList> getWhiteLists(){
        return whiteLists;
    }
    public void addWhiteList(NodePortTuple nodePortTuple,String flow){
        Date date=new Date();
        WhiteList whiteList=new WhiteList(nodePortTuple,date,flow);
        whiteLists.add(whiteList);
    }
}
