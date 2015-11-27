package com.sigchi.lapcounter;

/**
 * Created by tommypacker for HackIllinois' 2016 Clue Hunt
 */
public class DeviceItem {

    private String deviceName;
    private String address;

    public String getDeviceName() {
        return deviceName;
    }

    public String getAddress() {
        return address;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public DeviceItem(String name, String address){
        this.deviceName = name;
        this.address = address;
    }
}
