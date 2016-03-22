package com.example.thesamespace.gmble_zeng;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.util.ChartUtils;

/**
 * Created by thesamespace on 2016/1/15.
 */
public class MapActivity extends Activity implements View.OnClickListener, BeaconConsumer {
    private BluetoothAdapter bluetoothAdapter;
    private Button btn_Start;
    private Button btn_Test;
    private Button btn_ShowBeaconPoint;
    private Button btn_setTxPower;
    private Button btn_setN;
    private EditText edt_TxPower;
    private EditText edt_N;
    private TextView tv_Legend;
    private List<BLE> mBLEs = new ArrayList<BLE>();
    private int[] colors = {ChartUtils.COLORS[0], ChartUtils.COLORS[1], ChartUtils.COLORS[2], ChartUtils.COLORS[3], ChartUtils.COLORS[4], Color.BLUE, Color.BLACK, Color.GREEN, Color.RED, Color.GRAY, Color.DKGRAY};
    private int colorIndex = 0;
    private boolean clearMessage = false;
    private LinearLayout linearLayout;
    private MyView myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        init();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.stopLeScan(leScanCallback);
    }
    private void init() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btn_Start = (Button) findViewById(R.id.btn_Start);
        btn_ShowBeaconPoint = (Button) findViewById(R.id.btn_showbeacon);
        btn_Test = (Button) findViewById(R.id.btn_ShowDB);
        btn_setTxPower = (Button) findViewById(R.id.btn_setTxPower);
        btn_setN = (Button) findViewById(R.id.btn_setN);

        btn_Start.setOnClickListener(this);
        btn_ShowBeaconPoint.setOnClickListener(this);
        btn_Test.setOnClickListener(this);
        btn_setTxPower.setOnClickListener(this);
        btn_setN.setOnClickListener(this);

        edt_TxPower = (EditText) findViewById(R.id.edt_txPower);
        edt_N = (EditText) findViewById(R.id.edt_n);

        tv_Legend = (TextView) findViewById(R.id.tv_Legend);

        myView = (MyView) findViewById(R.id.myview);
        setMapSize();
        myView.setBitmap(R.drawable.img_map);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Start:
                if (btn_Start.getText().toString().equals("Start")) {
                    if (bluetoothAdapter.isEnabled()) {
                        btn_Start.setText("Stop");
                        bluetoothAdapter.startLeScan(leScanCallback);
                        Toast.makeText(this, "LeScan Start!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Please Turn On the Bluetooth First!", Toast.LENGTH_SHORT).show();
                    }
                } else if (btn_Start.getText().toString().equals("Stop")) {
                    btn_Start.setText("Start");
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    Toast.makeText(this, "LeScan Stop!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_showbeacon:
                myView.addBeaconPoint("05(8,8)", myView.getWidth() / 10 * 8, myView.getHeight() / 10 * 8);
                myView.addBeaconPoint("06(2,5)", myView.getWidth() / 10 * 2, myView.getHeight() / 10 * 5);
                myView.addBeaconPoint("07(8,3)", myView.getWidth() / 10 * 8, myView.getHeight() / 10 * 3);
                break;
            case R.id.btn_ShowDB:
                break;
            case R.id.btn_setTxPower:
                for (BLE ble : mBLEs) {
                    ble.setTxPower(Integer.parseInt(edt_TxPower.getText().toString()));
                }
                break;
            case R.id.btn_setN:
                for (BLE ble : mBLEs) {
                    ble.setN(Float.parseFloat(edt_N.getText().toString()));
                }
                break;
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            updateBLEList(device, rssi);
        }
    };

    @Override
    public void onBeaconServiceConnect() {

    }

    private void updateBLEList(BluetoothDevice device, int rssi) {
        if (clearMessage == true) {
            clearMessage = false;
            for (BLE ble : mBLEs) {
                ble.rssiList.clear();
                ble.rssiSum = 0;
                ble.setVariance(0);
            }
        }
        Boolean isExisted = false;
        for (int i = 0; i < mBLEs.size(); i++) {
            if (mBLEs.get(i).mDevice.getName().toString().equals(device.getName().toString())) {
                mBLEs.get(i).upDate(rssi);
                isExisted = true;
                break;
            }
        }
        if (isExisted == false) {
            BLE mBle = new BLE(device, rssi);
            mBle.setColor(colors[colorIndex]);
            colorIndex++;
            if (colorIndex > colors.length - 1) {
                colorIndex = 0;
            }
            mBLEs.add(mBle);
        }
        setLegend();
    }

    private void setLegend() {
//        Collections.sort(mBLEs);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String str = "";
                for (BLE ble : mBLEs) {
                    if (ble.isEnabled()) {
                        str += String.format("<font color=%d>%s %d  Variance:%.2f  Distance:%.2f</font><br>", ble.getColor(), ble.mDevice.getName(), ble.getLastRssi(), ble.getVariance(), ble.getDistance());
                    }
                }
                tv_Legend.setText(Html.fromHtml(str));
            }
        });
    }

    private void setMapSize() {
        WindowManager windowManager = this.getWindowManager();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) myView.getLayoutParams();
        int width = windowManager.getDefaultDisplay().getWidth() - 100;
        int height = windowManager.getDefaultDisplay().getHeight() - 100;
        if (width < height) {
            height = width;
        } else {
            width = height;
        }
        layoutParams.width = width;
        layoutParams.height = height;
        myView.setLayoutParams(layoutParams);
        myView.setRect2(new Rect(0, 0, width, height));
    }
}
