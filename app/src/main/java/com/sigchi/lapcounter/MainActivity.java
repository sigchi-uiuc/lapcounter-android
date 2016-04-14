package com.sigchi.lapcounter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String ADDRESS_1 = "00:1A:7D:DA:71:13";
    String ADDRESS_2 = "";

    String baseURL = "";
    RequestQueue queue;

    private String lastAddr;
    private int lapCount;
    private Boolean halfLap;
    private Boolean started;
    private long startTime;
    private double lapTime;
    ArrayList<Double> times;

    public static int REQUEST_BLUETOOTH = 1;
    private DeviceItemAdapter adapter;
    private ArrayList<DeviceItem> devices;
    private BluetoothLeScanner leScanner;

    TextView lapCountView;
    TextView lapTimeView;

    private final ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            BluetoothDevice device = result.getDevice();
            DeviceItem item = new DeviceItem(device.getName(), device.getAddress());
            if(device.getAddress().equals(ADDRESS_1) || device.getAddress().equals(ADDRESS_2)) {
                devices.add(item);
                adapter.notifyDataSetChanged();
                if(!device.getAddress().equals(lastAddr)) {
                    if(halfLap) {
                        lapCount++;
                        halfLap = false;
                        lapTime = (System.currentTimeMillis() - startTime) / 1000;
                        startTime = System.currentTimeMillis();
                        lastAddr = device.getAddress();
                        lapCountView.setText("Laps: " + lapCount);
                        lapTimeView.setText("Time: " + lapTime);
                        Log.d("Bluetooth", "Detected device " + device.getAddress());
                    }
                    else {
                        halfLap = true;
                        lastAddr = device.getAddress();
                        Log.d("Bluetooth", "Detected device" + device.getAddress());
                    }
                    if(!started) {
                        lapCountView.setText("Laps:" + lapCount);
                        startTime = System.currentTimeMillis();
                        lastAddr = device.getAddress();
                        Log.d("Bluetooth", "Detected device" + device.getAddress());
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        queue = Volley.newRequestQueue(this);

        lastAddr = "";
        lapCount = 0;
        halfLap = false;
        started = false;
        lapTime = 0;

        lapTimeView = (TextView) findViewById(R.id.lapTime);
        lapCountView = (TextView) findViewById(R.id.num_laps);

        times = new ArrayList<>();

        BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
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
                    lapCountView.setText("Searching for Device");
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

    /**
     * Use to send lap data to the server
     * @param user User who completed the lap
     * @param userLapTime Duration of the lap
     */
    private void sendLapDataToServer(String user, int userLapTime) {
        final String sendURL = baseURL + "/" + user + "/true/0/" + userLapTime;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, sendURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("sendLapDataToServer", sendURL);
                        Log.d("Response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("sendLapDataToServer", error.getMessage());
            }
        });
        queue.add(stringRequest);
    }

}
