package net.floodlightcontroller.trafficmonitor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.floodlightcontroller.core.types.NodePortTuple;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;
import net.floodlightcontroller.trafficmonitor.web.EventSerializer;
import org.projectfloodlight.openflow.types.U64;

import java.util.Date;

@JsonSerialize(using = EventSerializer.class)
public class Event {
    private Date time;                  //事件发生事件
    private NodePortTuple source;       //事件发生源头
    private U64 rxSpeed;                //端口输入流量
    private U64 txSpeed;                //端口输出流量
    private Strategy strategy;               //策略

    public Event() {
        time = new Date();
        source = null;
        rxSpeed = txSpeed = U64.ZERO;
        strategy = null;
    }

    public Event(SwitchPortBandwidth bw, Strategy strategy) {
        time = new Date();
        source = new NodePortTuple(bw.getSwitchId(), bw.getSwitchPort());
        rxSpeed = U64.of(bw.getBitsPerSecondRx().getValue());
        txSpeed = U64.of(bw.getBitsPerSecondTx().getValue());
        this.strategy = new Strategy(strategy);
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public NodePortTuple getSource() {
        return source;
    }

    public void setSource(NodePortTuple source) {
        this.source = source;
    }

    public U64 getRxSpeed() {
        return rxSpeed;
    }

    public void setRxSpeed(U64 rxSpeed) {
        this.rxSpeed = rxSpeed;
    }

    public U64 getTxSpeed() {
        return txSpeed;
    }

    public void setTxSpeed(U64 txSpeed) {
        this.txSpeed = txSpeed;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
}
