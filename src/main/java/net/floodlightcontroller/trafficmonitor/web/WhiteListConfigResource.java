package net.floodlightcontroller.trafficmonitor.web;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.trafficmonitor.ITrafficMonitorService;
import net.floodlightcontroller.trafficmonitor.TrafficController;
import net.floodlightcontroller.trafficmonitor.WhiteList;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;
import org.restlet.resource.Delete;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


public class WhiteListConfigResource extends ServerResource {
    //前端返回这个事件的端口信息
    @Post
    public String config(String json){
        ITrafficMonitorService trafficMonitorService = (ITrafficMonitorService) getContext().getAttributes().get(ITrafficMonitorService.class.getCanonicalName());
        LinkedList<WhiteList> list=trafficMonitorService.getWhiteLists();
        String str=removeBraces(json);
        String[] rows=str.split(",");
        Map<String, Object> retMap = new Gson().fromJson(
                json, new TypeToken<HashMap<String, Object>>() {}.getType()
        );
        String dpid=retMap.get("dpid").toString();
        String port_no=retMap.get("port_no").toString();
        Iterator<WhiteList> iterator = list.iterator();
        if (list.isEmpty()){
            NodePortTuple npt = new NodePortTuple(DatapathId.of(dpid), OFPort.of(Integer.parseInt(port_no)));
            String flow=TrafficController.linkPacket(dpid, port_no);
            trafficMonitorService.addWhiteList(npt,flow);
            return "{\"status\":\"WhiteList updated\"}";
        }
        else {
            while (iterator.hasNext()) {
                WhiteList iter=iterator.next();
                if (iter.getSource().getNodeId().toString().equals(dpid) && iter.getSource().getPortId().toString().equals(port_no)) {
                    return "{\"status\":\"WhiteList is existed\"}";
                } else {
                    NodePortTuple npt = new NodePortTuple(DatapathId.of(dpid), OFPort.of(Integer.parseInt(port_no)));
                    String flow=TrafficController.linkPacket(dpid, port_no);
                    trafficMonitorService.addWhiteList(npt,flow);
                    return "{\"status\":\"WhiteList updated\"}";
                }
            }
        }
        return "{\"status\":\"WhiteList finished\"}";
    }

    @Delete
    public String deleteList(String json){
        String str=removeBraces(json);
        String[] rows=str.split(",");
        Map<String, Object> retMap = new Gson().fromJson(
                json, new TypeToken<HashMap<String, Object>>() {}.getType()
        );
        String flow=retMap.get("flow").toString();

        TrafficController.deleteFlow(flow);
        ITrafficMonitorService trafficMonitorService = (ITrafficMonitorService) getContext().getAttributes().get(ITrafficMonitorService.class.getCanonicalName());
        LinkedList<WhiteList> list=trafficMonitorService.getWhiteLists();
        Iterator<WhiteList> iterator = list.iterator();
        String dpid=retMap.get("dpid").toString();
        String port_no=retMap.get("port_no").toString();
        while (iterator.hasNext()) {
            WhiteList iter = iterator.next();
            if (iter.getSource().getNodeId().toString().equals(dpid) && iter.getSource().getPortId().toString().equals(port_no)) {
                iterator.remove();
                break;
            }
        }
        return "{\"status\":\"WhiteList deleted\"}";
    }
    private String removeBraces(String string){
        String result = null;
        if(string.charAt(0) == '{' && string.charAt(string.length()-1) == '}'){
            result = string.substring(1, string.length()-1);
        }
        return result;
    }
}
