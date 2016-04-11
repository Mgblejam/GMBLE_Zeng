package com.example.thesamespace.gmble_zeng;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thesamespace on 2016/4/11.
 */
public class CollectRSSIActivity extends Activity implements View.OnClickListener {
    private TextView tv_rssiCount;
    private EditText edt_distance;
    private Button btn_start;
    private List<BLEData> BLEDataList = new ArrayList<>();
    private String distance = "1";
    private MyLeScaner myLeScaner = new MyLeScaner() {
        @Override
        protected void mOnLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            boolean exitFlage = false;
            for (int i = 0; i < BLEDataList.size(); i++) {
                if (BLEDataList.get(i).bleName.equals(device.getName())) {
                    exitFlage = true;
                    BLEDataList.get(i).rssiStrList.add(distance + ":" + rssi + ",");
                }
            }
            if (!exitFlage) {
                BLEDataList.add(new BLEData(device.getName(), distance, String.valueOf(rssi)));
            }
            showRssiCount();
        }

        @Override
        protected void printLog(String str) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Collect RSSI");
        setContentView(R.layout.activity_collectrssi);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        save();
        myLeScaner.startLeScan();
    }

    private void initView() {
        btn_start = (Button) findViewById(R.id.btn_start);
        Button btn_save = (Button) findViewById(R.id.btn_save);
        Button btn_setDistance = (Button) findViewById(R.id.btn_setDistance);

        btn_start.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_setDistance.setOnClickListener(this);

        tv_rssiCount = (TextView) findViewById(R.id.tv_rssiCount);
        edt_distance = (EditText) findViewById(R.id.edt_distance);
        distance = edt_distance.getText().toString();
    }

    private void save() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SDFileHelper sdFileHelper = new SDFileHelper(CollectRSSIActivity.this);
                for (BLEData bleData : BLEDataList) {
                    String string = "";
                    for (String str : bleData.rssiStrList) {
                        string += str;
                    }
                    try {
                        sdFileHelper.savaFileToSD(bleData.bleName + ".txt", string);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void showRssiCount() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_rssiCount.setText("已收集Rssi数据数量(" + distance + "m):");
                for (BLEData bleData : BLEDataList) {
                    tv_rssiCount.append("\n" + bleData.bleName + ":" + bleData.rssiStrList.size());
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                if (btn_start.getText().toString().equals("Start")) {
                    btn_start.setText("Stop");
                    myLeScaner.startLeScan();
                } else {
                    btn_start.setText("Start");
                    myLeScaner.stopLeScan();
                }
                break;
            case R.id.btn_save:
                save();
                break;
            case R.id.btn_setDistance:
                distance = edt_distance.getText().toString();
                break;
        }
    }

    class BLEData {
        String bleName;
        List<String> rssiStrList = new ArrayList<>();

        BLEData(String bleName, String distance, String rssi) {
            this.bleName = bleName;
            rssiStrList.add(distance + ":" + rssi + ",");
        }
    }
}
