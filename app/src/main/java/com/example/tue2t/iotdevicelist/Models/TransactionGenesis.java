package com.example.tue2t.iotdevicelist.Models;

/**
 * Created by tue2t on 9/10/2017.
 */

public class TransactionGenesis {
    String DHKey = "";
    String deviceName = "New Device";
    String deviceIP;
    String devicePort;


    public TransactionGenesis(String deviceName, String deviceIP, String devicePort) {
        this.deviceName = deviceName;
        this.devicePort = devicePort;
        this.deviceIP = deviceIP;
    }

    public void addDHkey(String key) {
        this.DHKey = key;
    }

    public void addDeviceName(String name) {
        deviceName = name;
    }

    @Override
    public String toString() {
        return "Genesis Trans : " + deviceName + " [" + DHKey + "]";
    }
}
