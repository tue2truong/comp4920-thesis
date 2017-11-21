package com.example.tue2t.iotdevicelist.Models;

import java.io.Serializable;

/**
 * Created by tue on 6/10/2017.
 */

public class Transaction implements Serializable{
    private String dateString;
    private String data;
    private String type;

    public Transaction(String dateString, String data, String type) {
        this.dateString = dateString;
        this.data = data;
        this.type = type;
    }

    @Override
    public String toString() {
        return "\nString: " + dateString + "\n" +
                "Data: " + data + "\n" +
                "Type:" + type + "\n";
    }
}
