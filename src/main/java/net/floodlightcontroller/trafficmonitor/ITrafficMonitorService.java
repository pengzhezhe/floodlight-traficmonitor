package net.floodlightcontroller.trafficmonitor;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.U64;


import java.util.LinkedList;
import java.util.Map;

public interface ITrafficMonitorService extends IFloodlightService {
    //获取所有交换机的带宽状态
    public Map<NodePortTuple, SwitchPortBandwidth> getBandwidthMap();
    //获取指定交换机的带宽状态
    public SwitchPortBandwidth getBandwidth(DatapathId dpid, OFPort port);
    //获取策略
    public Strategy getStrategy();
    //获取事件
    public LinkedList<Event> getEvents();
    //设置策略
    public void setStrategy(U64 maxSpeed, String action, long actionDuring);
    //查看白名单
    public LinkedList<WhiteList> getWhiteLists();
    //加入白名单
    public void addWhiteList(NodePortTuple nodePortTuple, String flow);
}
