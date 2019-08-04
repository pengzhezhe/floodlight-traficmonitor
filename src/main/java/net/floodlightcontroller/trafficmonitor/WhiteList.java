package net.floodlightcontroller.trafficmonitor;

import net.floodlightcontroller.core.types.NodePortTuple;

import java.util.Date;

public class WhiteList {
    private NodePortTuple source;
    private Date time;
    private String flow;

    public WhiteList() {
    }

    public WhiteList(NodePortTuple source, Date time, String flow) {
        this.source = source;
        this.time = time;
        this.flow = flow;
    }

    public NodePortTuple getSource() {
        return source;
    }

    public void setSource(NodePortTuple source) {
        this.source = source;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }
}
