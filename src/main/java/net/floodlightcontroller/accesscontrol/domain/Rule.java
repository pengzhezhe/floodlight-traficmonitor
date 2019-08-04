package net.floodlightcontroller.accesscontrol.domain;

/**
 * 规则持久类
 */
public class Rule {
    /**
     * 规则ID
     */
    private int ruleId;

    /**
     * 用户等级
     */
    private int userLevel;

    /**
     * 设备类型 1-PC 2-服务器 3-外网
     */
    private int deviceType;

    /**
     * 目标用户等级
     */
    private int destUserLevel;

    /**
     * 目的设备类型
     */
    private int destDeviceType;

    /**
     * 操作 deny permit
     */
    private String action;

    @Override
    public String toString() {
        return "Rule{" +
                "ruleId=" + ruleId +
                ", userLevel=" + userLevel +
                ", deviceType=" + deviceType +
                ", destUserLevel=" + destUserLevel +
                ", destDeviceType=" + destDeviceType +
                ", action='" + action + '\'' +
                '}';
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDestUserLevel() {
        return destUserLevel;
    }

    public void setDestUserLevel(int destUserLevel) {
        this.destUserLevel = destUserLevel;
    }

    public int getDestDeviceType() {
        return destDeviceType;
    }

    public void setDestDeviceType(int destDeviceType) {
        this.destDeviceType = destDeviceType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

