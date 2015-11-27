package com.sigchi.lapcounter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tommypacker for HackIllinois' 2016 Clue Hunt
 */
public class DeviceItemAdapter extends ArrayAdapter<DeviceItem> {

    public DeviceItemAdapter(Context context, ArrayList<DeviceItem> devices){
        super(context, 0, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DeviceItem device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_item_layout, parent, false);
        }
        // Lookup view for data population
        TextView macAddress = (TextView) convertView.findViewById(R.id.deviceAddress);
        // Populate the data into the template view using the data object
        macAddress.setText(device.getAddress());
        // Return the completed view to render on screen
        return convertView;
    }
}
