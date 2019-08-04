package net.floodlightcontroller.trafficmonitor;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.linkdiscovery.Link;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;
import org.projectfloodlight.openflow.protocol.*;
import org.projectfloodlight.openflow.protocol.meterband.OFMeterBand;
import org.projectfloodlight.openflow.protocol.meterband.OFMeterBandDrop;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.U64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class TrafficController {
    private static final Logger logger = LoggerFactory.getLogger(TrafficController.class);
    private static final String URL_ADD_DELETE_FLOW="http://localhost:8080/wm/staticentrypusher/json";
    private static int countFlow = 0;

    public static void execute(IOFSwitchService switchService, HashMap<NodePortTuple, SwitchPortBandwidth> exceptionLog, Strategy strategy, LinkedList<Event> events,HashMap<NodePortTuple, Date> addFlowEntryHistory,ILinkDiscoveryService linkDiscoveryService){
        for(Map.Entry<NodePortTuple, SwitchPortBandwidth> e:exceptionLog.entrySet()){
            NodePortTuple npt=e.getKey();
            SwitchPortBandwidth bandwidth=e.getValue();
            //获取节点所属交换机
            IOFSwitch sw=switchService.getSwitch(npt.getNodeId());

            //如果没有下发过流表
            if(!addFlowEntryHistory.containsKey(npt)){
                switch(strategy.getAction()) {
                    case Strategy.ACTION_DROP:
                        if (!isPortConnectedToSwitch(sw.getId(), npt.getPortId(), linkDiscoveryService)) {
                            int hardTimeout = (int) strategy.getActionDuration();
                            dropPacket(sw, npt.getPortId().getPortNumber(), hardTimeout, countFlow++);
                            events.add(new Event(bandwidth, strategy));
                            addFlowEntryHistory.put(npt, new Date());
                        }
                        break;
                }
            }else{
                //已经向该端口下发过流表项，检测该流表项是否过期
                Date currentTime = new Date();
                long period = (currentTime.getTime() - addFlowEntryHistory.get(npt).getTime()) / 1000;	// 换算成second
                if(strategy.getActionDuration() > 0  && period > strategy.getActionDuration()){
                    logger.info("flow {match:" + npt.getNodeId() + " / " + npt.getPortId() + ", action:drop} expired!");
                    addFlowEntryHistory.remove(npt);
                }
            }
        }
    }
    public static String linkPacket(String dpid,String port_no){
        HashMap<String,String> flow=new HashMap<String,String>();
        flow.put("switch",dpid);
        flow.put("name","flow"+countFlow++);
        flow.put("in_port",String.valueOf(port_no));
        flow.put("priority","32768");
        flow.put("cookie","0");
        flow.put("active","true");
        flow.put("actions","output=all");
        String result=addFlow(flow);
        logger.info(result);
        int count=countFlow-1;
        return "flow"+count;
    }
    //丢包操作
    public static void dropPacket(IOFSwitch sw,int in_port,int hard_timeout,int count){
        HashMap<String,String> flow=new HashMap<String,String>();
        flow.put("switch",sw.getId().toString());
        flow.put("name","flow"+count);
        flow.put("in_port",String.valueOf(in_port));
        //flow.put("hard_timeout",String.valueOf(hard_timeout));默认不会超时
        flow.put("priority","32000");
        flow.put("cookie","0");
        flow.put("active","true");
        flow.put("hard_timeout", String.valueOf(hard_timeout));
        String result=addFlow(flow);
        logger.info(result);
    }
    //添加流表
    public static String addFlow(HashMap<String,String> flow){
        String result=sendPost(URL_ADD_DELETE_FLOW,hashMapToJson(flow));
        return result;
    }

    //删除流表
    public static String deleteFlow(String flowName)
    {
        String flow="{\"name\":\""+flowName+"\"}";
        String result= sendDelete(URL_ADD_DELETE_FLOW, flow);
        return result;
    }
    //发送POST请求
    public static String sendPost(String url,String param){
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        }catch (Exception e){
            System.out.println("发送post异常！"+e);
            e.printStackTrace();
        }
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;

    }

    public static String sendDelete(String url,String param){
        String result="";
        PrintWriter out=null;
        BufferedReader in=null;
        try {
            URL realUrl=new URL(url);
            HttpURLConnection conn= (HttpURLConnection) realUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("DELETE");
            out=new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line=in.readLine())!=null){
                result+=line;
            }
        } catch (IOException e) {
            System.out.println("发送DELETE请求出现异常！"+e);
            e.printStackTrace();
        }finally {
            try {
                if(out!=null){
                    out.close();
                }
                if (in!=null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //hashMap转成json格式
    public static String hashMapToJson(HashMap map) {
        String string = "{";
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry e = (Map.Entry) it.next();
            string += "\"" + e.getKey() + "\":";
            string += "\"" + e.getValue() + "\",";
        }
        string = string.substring(0, string.lastIndexOf(","));
        string += "}";
        logger.info(string);
        return string;
    }

    /**
     * 检查交换机sw端口相连的是否为交换机
     * @return
     */
    private static boolean isPortConnectedToSwitch(DatapathId dpid, OFPort port, ILinkDiscoveryService linkDiscoveryService){
        boolean result = false;

        Map linksMap = linkDiscoveryService.getLinks();	/* 获取交换机之间的链路 */
        Set keys = linksMap.keySet();
        Iterator it = keys.iterator();
        while(it.hasNext()){
            Link link = (Link)it.next();
            /* 检查该交换机链路中是否含有dpid和portNumber*/
            result = (link.getSrc().equals(dpid) && link.getSrcPort().equals(port)) || (link.getDst().equals(dpid) && link.getDstPort().equals(port) )? true : false;
        }
        return result;
    }


    public static void rateLimit(IOFSwitch sw, int ingressPortNo, int hardTimeout, long meterId, int countFlow){
        //解析传进来的属性
        HashMap<String, String> flow = new HashMap<String, String>();
        flow.put("switch", sw.getId().toString());
        flow.put("name", "flow" + countFlow);
        flow.put("in_port", String.valueOf(ingressPortNo));
        flow.put("cookie", "0");
        flow.put("priority", "32768");
        flow.put("active", "true");
        flow.put("hard_timeout", String.valueOf(hardTimeout));
        flow.put("instruction_goto_meter", String.valueOf(meterId));
        String result = addFlow(flow);
        logger.info(result);
    }


    public static long addMeter(IOFSwitch sw, U64 rateLimit, long burstSize){
        logger.info("enter add meter()");
        OFFactory my13Factory = OFFactories.getFactory(OFVersion.OF_13);
        /* 设置flag */
        Set<OFMeterFlags> flags = new HashSet<OFMeterFlags>();
        flags.add(OFMeterFlags.KBPS);
//		flags.add(OFMeterFlags.BURST);

        long meterId = 1;

        /* rateLimit（Bps）转换成rate（kbps）*/
        long rate = (rateLimit.getValue() * 8) / 1000;

        /* 创建一个band */
        OFMeterBandDrop bandDrop = my13Factory.meterBands().buildDrop()
                .setRate(rate)	// kbps
//														   .setBurstSize(0)
                .build();
        logger.info("create band");

        /* 设置bands */
        List<OFMeterBand> bands = new ArrayList<OFMeterBand>();
        bands.add(bandDrop);
        logger.info("add band to bands");

        /* 创建一个Meter Modification Message发给交换机 */
        OFMeterMod meterMod = my13Factory.buildMeterMod()
                .setMeterId(meterId)
                .setCommand(OFMeterModCommand.ADD)
                .setFlags(flags)
                .setMeters(bands)
                .build();
        logger.info("create meterMod msg");

        if( sw.write(meterMod) ){
            logger.info("add meter" + meterId + "to meter table");
            return meterId;
        }

        else{
            logger.info("add meter failed");
            return 0;
        }
    }
}