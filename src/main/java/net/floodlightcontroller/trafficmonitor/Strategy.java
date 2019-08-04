package net.floodlightcontroller.trafficmonitor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.floodlightcontroller.trafficmonitor.web.StrategySerializer;
import org.projectfloodlight.openflow.types.U64;

@JsonSerialize(using= StrategySerializer.class)
public class Strategy {
    public static final String ACTION_DROP="drop";
    public static final String ACTION_LIMIT="limit";


    private U64 maxSpeed;           //阈值
    private String action;          //执行操作
    private long actionDuration;     //操作持续时间

    public Strategy(){
        maxSpeed=U64.of(3000);
        action=ACTION_DROP;
        actionDuration=500;
    }

    public Strategy(Strategy strategy){
        this.maxSpeed = strategy.getMaxSpeed();
        this.action = strategy.getAction();
        this.actionDuration = strategy.getActionDuration();
    }

    public U64 getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(U64 maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getActionDuration() {
        return actionDuration;
    }

    public void setActionDuration(long actionDuration) {
        this.actionDuration = actionDuration;
    }


}
