package net.floodlightcontroller.trafficmonitor;

import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;
import org.projectfloodlight.openflow.types.U64;

import java.util.HashMap;
import java.util.Map;

public class TrafficAnalyzer {
    public static void Analysis(HashMap<NodePortTuple, SwitchPortBandwidth> bandwidth,Map<NodePortTuple, SwitchPortBandwidth> exceptionLog,Strategy strategy){
        U64 rxSpeed = U64.ZERO;
        U64 txSpeed = U64.ZERO;
        U64 maxSpeed=strategy.getMaxSpeed();
        for(Map.Entry<NodePortTuple,SwitchPortBandwidth> e:bandwidth.entrySet()){
            rxSpeed=U64.of(e.getValue().getBitsPerSecondRx().getValue()/8);
            txSpeed=U64.of(e.getValue().getBitsPerSecondRx().getValue()/8);
            if(rxSpeed.compareTo(maxSpeed)>0|| txSpeed.compareTo(maxSpeed) > 0){
                exceptionLog.put(e.getKey(),e.getValue());
                System.out.println("close!!");
            }
        }
    }
}
