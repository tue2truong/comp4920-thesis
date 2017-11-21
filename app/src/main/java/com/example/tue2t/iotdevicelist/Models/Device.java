package com.example.tue2t.iotdevicelist.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by tue on 6/10/2017.
 */

public class Device implements Serializable{
    private String deviceName;
    private String deviceIP;
    private String devicePort;
    private String deviceID;
    private ArrayList<Transaction> transactionList;

    // constructor
    public Device (String deviceName, String deviceIP, String devicePort, String deviceID, ArrayList<Transaction> transactionList) {
        this.deviceName = deviceName;
        this.deviceIP = deviceIP;
        this.devicePort = devicePort;
        this.deviceID = deviceID;
        this.transactionList = transactionList;
    }

    //getter
    public String getDeviceName() {
        return this.deviceName;
    }

    public String getDeviceIP() {
        return this.deviceIP;
    }

    public String getDevicePort() {
        return this.devicePort;
    }

    public String getDeviceID() {
        return this.deviceID;
    }

    public ArrayList<Transaction> getTransactionList() {
        return this.transactionList;
    }

    @Override
    public String toString() {
        return deviceName;
    }
}
