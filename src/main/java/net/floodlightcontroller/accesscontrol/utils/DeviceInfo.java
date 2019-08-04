package net.floodlightcontroller.accesscontrol.utils;

public class DeviceInfo {
    private int userLevel;

    private int deviceType;

    private String macAddress;

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "userLevel=" + userLevel +
                ", deviceType=" + deviceType +
                ", macAddress='" + macAddress + '\'' +
                '}';
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

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
