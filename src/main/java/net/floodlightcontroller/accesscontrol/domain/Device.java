package net.floodlightcontroller.accesscontrol.domain;

/**
 * 设备持久类
 */
public class Device {
    /**
     * 设备id 主键
     */
    private int deviceId;

    /**
     * 设备MAC地址
     */
    private String macAddress;

    /**
     * 设备类型 1-PC 2-服务器 3-外网
     */
    private int deviceType;

    /**
     * 用户id 外键
     */
    private int userId;

    /**
     * 关联的User对象
     */
    private User user;

    @Override
    public String toString() {
        return "Device{" +
                "deviceId=" + deviceId +
                ", macAddress='" + macAddress + '\'' +
                ", deviceType=" + deviceType +
                ", userId=" + userId +
                ", user=" + user +
                '}';
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
