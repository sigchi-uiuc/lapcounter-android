package com.sigchi.lapcounter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pwittchen.reactivebeacons.library.Beacon;
import com.github.pwittchen.reactivebeacons.library.ReactiveBeacons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String ITEM_FORMAT = "MAC: %s";
    private ReactiveBeacons reactiveBeacons;
    private Subscription subscription;
    private ListView deviceList;
    private Map<String, Beacon> beacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reactiveBeacons = new ReactiveBeacons(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!reactiveBeacons.isBluetoothEnabled()) {
            reactiveBeacons.requestBluetoothAccess(this);
        }

        beacons = new HashMap<>();
        deviceList = (ListView) findViewById(R.id.deviceList);
        Button startScan = (Button) findViewById(R.id.startScan);
        startScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscription = reactiveBeacons.observe()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Beacon>() {
                            @Override public void call(Beacon beacon) {
                                // do something with beacon
                                beacons.put(beacon.device.getAddress(), beacon);
                                refreshBeaconList();
                            }
                        });
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!reactiveBeacons.isBleSupported()) { // optional, but recommended step
            // show message for the user that BLE is not supported on the device
            Log.e("Support", "Device is not supported");
            Toast.makeText(this, "BLE is not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private void refreshBeaconList() {

        List<String> list = new ArrayList<>();

        for (Beacon beacon : beacons.values()) {
            list.add(getBeaconItemString(beacon));
        }

        int itemLayoutId = android.R.layout.simple_list_item_1;
        deviceList.setAdapter(new ArrayAdapter<>(this, itemLayoutId, list));
    }

    private String getBeaconItemString(Beacon beacon) {
        String mac = beacon.device.getAddress();
        return String.format(ITEM_FORMAT, mac);
    }

}
