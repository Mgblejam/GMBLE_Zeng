package com.example.thesamespace.gmble_zeng;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by thesamespace on 2016/1/13.
 */
public class ChartTestActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Button btn_StartStopChart;
    private Button btn_ClearChart;
    private Button btn_Test;
    private TextView tv_Legend;
    private LineChartView mLineChartView;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BLE> mBLEs = new ArrayList<BLE>();
    private int[] colors = {ChartUtils.COLORS[0], ChartUtils.COLORS[1], ChartUtils.COLORS[2], ChartUtils.COLORS[3], ChartUtils.COLORS[4], Color.BLUE, Color.BLACK, Color.GREEN, Color.RED, Color.GRAY, Color.DKGRAY};
    private int colorIndex = 0;
    private boolean clearMessage = false;
    private LinearLayout linearLayout;
    private int checkboxID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charttest);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_StartStopChart:
                if (btn_StartStopChart.getText().equals("Start")) {
                    btn_StartStopChart.setText("Stop");
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                    Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
                } else {
                    btn_StartStopChart.setText("Start");
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_ClearChart:
                clearMessage = true;
                break;
            case R.id.btn_Test:
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (BLE ble : mBLEs) {
            if (ble.mDevice.getName().equals(buttonView.getText().toString())) {
                ble.setEnabled(buttonView.isChecked());
                break;
            }
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            updateBLEList(device, rssi);
        }
    };

    private void init() {
        linearLayout = (LinearLayout) findViewById(R.id.layout_checkboxs);
        btn_StartStopChart = (Button) findViewById(R.id.btn_StartStopChart);
        btn_ClearChart = (Button) findViewById(R.id.btn_ClearChart);
        btn_Test = (Button) findViewById(R.id.btn_Test);
        btn_StartStopChart.setOnClickListener(this);
        btn_ClearChart.setOnClickListener(this);
        btn_Test.setOnClickListener(this);
        tv_Legend = (TextView) findViewById(R.id.tv_Legend01);
        mLineChartView = (LineChartView) findViewById(R.id.linechartview);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "No Bluetooth", Toast.LENGTH_SHORT).show();
        }
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
            createCheckBox(device.getName());
        }
        drawLine();
    }

    private void drawLine() {
        final LineChartData mData = new LineChartData();
        List<Line> mLines = new ArrayList<Line>();
        for (BLE ble : mBLEs) {
            if (ble.isEnabled()) {
                List<PointValue> mPointValues_rssi = new ArrayList<PointValue>();
                for (int i = 0; i < ble.rssiList.size(); i++) {
                    mPointValues_rssi.add(new PointValue(i, Float.parseFloat(ble.rssiList.get(i).toString())));
                }
                Line mLine = new Line(mPointValues_rssi);
                mLine.setColor(ble.getColor());
                mLine.setHasPoints(false);
                mLine.setStrokeWidth(1);
                mLines.add(mLine);
            }
        }

        Axis axisX = new Axis();
        axisX.setName("Time");
        axisX.setMaxLabelChars(10);

        Axis axisY = new Axis();
        axisY.setHasLines(true);
        axisY.setMaxLabelChars(10);

        mData.setAxisXBottom(axisX);
        mData.setAxisYLeft(axisY);
        mData.setLines(mLines);
        setLegend();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLineChartView.setLineChartData(mData);
            }
        });
    }

    private void setLegend() {
//        Collections.sort(mBLEs);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String str = "";
                for (BLE ble : mBLEs) {
                    str += "<font color=" + ble.getColor() + ">" + ble.mDevice.getName() + " " + ble.getLastRssi() + "     Variance:" + String.format("%.2f", ble.getVariance()) + "</font><br>";
                }
                tv_Legend.setText(Html.fromHtml(str));
            }
        });
    }

    private void createCheckBox(String name) {
        final CheckBox checkBox = new CheckBox(this);
        checkBox.setText(name);
        checkBox.setChecked(true);
        checkBox.setId(checkboxID);
        checkboxID++;
        checkBox.setOnCheckedChangeListener(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                linearLayout.addView(checkBox);
            }
        });
    }

}
