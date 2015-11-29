package com.sigchi.lapcounter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static int REQUEST_BLUETOOTH = 1;
    private DeviceItemAdapter adapter;
    private ArrayList<DeviceItem> devices;
    private BluetoothAdapter BTAdapter;
    private BluetoothLeScanner leScanner;

    private final ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            DeviceItem item = new DeviceItem(device.getName(), device.getAddress());
            devices.add(item);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        leScanner = BTAdapter.getBluetoothLeScanner();

        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

        ListView deviceList = (ListView) findViewById(R.id.deviceList);
        devices = new ArrayList<>();
        if(devices.size() == 0) {
            devices.add(new DeviceItem("No Devices", "No devices"));
        }
        adapter = new DeviceItemAdapter(getApplicationContext(), devices);
        deviceList.setAdapter(adapter);

        ToggleButton startScan = (ToggleButton) findViewById(R.id.startScan);
        startScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.clear();
                    leScanner.startScan(callback);
                    Log.i("Start Scan", "Now Scanning for devices");
                    Toast.makeText(getApplicationContext(), "Now scanning", Toast.LENGTH_LONG).show();
                } else {
                    leScanner.stopScan(callback);
                }
            }
        });
    }



}
