package net.floodlightcontroller.trafficmonitor.web;

import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;
import net.floodlightcontroller.trafficmonitor.ITrafficMonitorService;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrafficMonitorResource extends ServerResource {
    private static final Logger logger= LoggerFactory.getLogger(TrafficMonitorResource.class);

    @Get("json")
    public Object retrieve(){
        ITrafficMonitorService trafficMonitorService=(ITrafficMonitorService)getContext().getAttributes().
                get(ITrafficMonitorService.class.getCanonicalName());
        String dpid_str=(String)getRequestAttributes().get(TrafficMonitorWebRoutable.DPID_STR);
        String port_str=(String)getRequestAttributes().get(TrafficMonitorWebRoutable.PORT_STR);

        //dpid和port初始化，NONE相当于所有交换机
        DatapathId dpid=DatapathId.NONE;
        OFPort port=OFPort.ALL;

        //dpid和port转成DatapathId
       if(!dpid_str.trim().equalsIgnoreCase("all")){
            try{
                dpid=DatapathId.of(dpid_str);
            }catch (Exception e){
                logger.error("wrong parse{}",dpid_str);
            }
        }
        if(!port_str.trim().equalsIgnoreCase("all")){
            try {
                port=OFPort.of(Integer.parseInt(port_str));
            }catch (Exception e){
                logger.error("wrong parse {}",port_str);
            }
        }

        Set<SwitchPortBandwidth> bandwidthSet=new HashSet<SwitchPortBandwidth>();

        //提供查找特定交换机特定端口和查找所有交换机所有端口两种情况
        if(!dpid.equals(DatapathId.NONE)&&!port.equals(OFPort.ALL)){
            bandwidthSet.add(trafficMonitorService.getBandwidth(dpid,port));
            return bandwidthSet;
        }
        else{
            for(Map.Entry<NodePortTuple,SwitchPortBandwidth> e:trafficMonitorService.getBandwidthMap().entrySet()){
                bandwidthSet.add(e.getValue());
            }
            return bandwidthSet;
        }

    }
}
