package com.example.tue2t.iotdevicelist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tue2t.iotdevicelist.Models.Device;

import java.util.ArrayList;

/**
 * Created by tue2t on 9/10/2017.
 */

public class DeviceAdapter extends BaseAdapter {
    Activity context;
    ArrayList<Device> deviceList;
    private static LayoutInflater inflater = null;

    public DeviceAdapter(Activity context, ArrayList<Device> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Device getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        itemView = (itemView == null) ? inflater.inflate(R.layout.list_item, null): itemView;
        TextView textViewName = (TextView) itemView.findViewById(R.id.textView_devName);
        TextView textViewIP = (TextView) itemView.findViewById(R.id.textView_ipAdr);

        Device selectedDev = deviceList.get(position);
        textViewName.setText("ID = " + selectedDev.getDeviceID() + "______" + selectedDev.getDeviceName());
        textViewIP.setText(selectedDev.getDeviceIP() + " : " + selectedDev.getDevicePort());

        return itemView;
    }
}
