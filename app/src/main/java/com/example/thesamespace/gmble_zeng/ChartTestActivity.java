package com.example.thesamespace.gmble_zeng;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
    private Button btn_ShowDB;
    private Button btn_ClearDB;
    private Button btn_setting;
    private Button btn_setTxPower;
    private Button btn_setN;
    private Button btn_Test;
    private EditText edt_TxPower;
    private EditText edt_N;
    private TextView tv_Legend;
    private TextView tv_rssiCount;
    private EditText edit_distance;
    private LineChartView mLineChartView;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BLE> mBLEs = new ArrayList<BLE>();
    private int[] colors = {ChartUtils.COLORS[0], ChartUtils.COLORS[1], ChartUtils.COLORS[2], ChartUtils.COLORS[3], ChartUtils.COLORS[4], Color.BLUE, Color.BLACK, Color.GREEN, Color.RED, Color.GRAY, Color.DKGRAY};
    private int colorIndex = 0;
    private boolean clearMessage = false;
    private LinearLayout linearLayout;
    private int checkboxID = 0;
    private MySQLite mySQLite;
    private SQLiteDatabase sqLiteDatabase;
    private int distance = 1;
    private int rssi = -60;
    private int txPower = -60;
    private float n = 0.3f;
    private Context context;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charttest);
        init();
        context = getApplicationContext();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    private void init() {
        linearLayout = (LinearLayout) findViewById(R.id.layout_checkboxs);
        btn_StartStopChart = (Button) findViewById(R.id.btn_StartStopChart);
        btn_ClearChart = (Button) findViewById(R.id.btn_ClearChart);
        btn_ShowDB = (Button) findViewById(R.id.btn_ShowDB);
        btn_ClearDB = (Button) findViewById(R.id.btn_ClearDataBase);
        btn_setting = (Button) findViewById(R.id.btn_setting);
        btn_setTxPower = (Button) findViewById(R.id.btn_setTxPower);
        btn_setN = (Button) findViewById(R.id.btn_setN);
        btn_Test = (Button) findViewById(R.id.btn_test);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        btn_StartStopChart.setOnClickListener(this);
        btn_ClearChart.setOnClickListener(this);
        btn_ShowDB.setOnClickListener(this);
        btn_ClearDB.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        btn_setTxPower.setOnClickListener(this);
        btn_setN.setOnClickListener(this);
        btn_Test.setOnClickListener(this);

        edt_TxPower = (EditText) findViewById(R.id.edt_txPower);
        edt_N = (EditText) findViewById(R.id.edt_n);

        tv_Legend = (TextView) findViewById(R.id.tv_Legend01);
        tv_rssiCount = (TextView) findViewById(R.id.tv_rssiCount);
        edit_distance = (EditText) findViewById(R.id.edt_distance);

        mLineChartView = (LineChartView) findViewById(R.id.linechartview);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "No Bluetooth", Toast.LENGTH_SHORT).show();
        }
//        mySQLite = new MySQLite(this, "BLE.db", null, 2);
//        sqLiteDatabase = mySQLite.getWritableDatabase();
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
            case R.id.btn_ShowDB:
                StringBuilder sb = new StringBuilder();
                Cursor cursor = sqLiteDatabase.query("MYBLE_00005", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
//                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        int distance = cursor.getInt(cursor.getColumnIndex("distance"));
                        int rssi = cursor.getInt(cursor.getColumnIndex("rssi"));
                        sb.append(String.format("ID:%d distance:%d rssi:%d \n", id, distance, rssi));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_ClearDataBase:
                sqLiteDatabase.delete("MYBLE_00005", null, null);
                sqLiteDatabase.delete("MYBLE_00006", null, null);
                sqLiteDatabase.delete("MYBLE_00007", null, null);
                sqLiteDatabase.delete("MYBLE02", null, null);
                break;
            case R.id.btn_setting:
                distance = Integer.parseInt(edit_distance.getText().toString());
                break;
            case R.id.btn_setTxPower:
                txPower = Integer.parseInt(edt_TxPower.getText().toString());
                break;
            case R.id.btn_setN:
                n = Float.parseFloat(edt_N.getText().toString());
                break;
            case R.id.btn_test:
                double distance = Math.pow(10, (rssi - txPower) / -10 * n);
                Toast.makeText(this, "" + distance, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_save:
                save();
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
            showRssiCount();
//            updateSQLite(device.getName(), distance, rssi);
        }
    };

    private void updateBLEList(BluetoothDevice device, int rssi) {
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
//            mySQLite.createTable(sqLiteDatabase, device.getName());
        }
//        drawLine();
        if (clearMessage == true) {
            clearMessage = false;
            for (BLE ble : mBLEs) {
                ble.rssiList.clear();
                ble.rssiAVGList.clear();
                ble.rssiSum = 0;
                ble.setVariance(0);
            }
        }
    }

    private void drawLine() {
        final LineChartData mData = new LineChartData();
        List<Line> mLines = new ArrayList<Line>();
        for (BLE ble : mBLEs) {
            if (ble.isEnabled()) {
                List<PointValue> mPointValues_rssi = new ArrayList<PointValue>();
                for (int i = 0; i < ble.rssiAVGList.size(); i++) {
                    mPointValues_rssi.add(new PointValue(i, ble.rssiList.get(i)));
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
        Collections.sort(mBLEs);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String str = "";
                for (BLE ble : mBLEs) {
                    if (ble.isEnabled()) {
                        str += String.format("<font color=%d>%s %d - Variance:%.2f</font><br>", ble.getColor(), ble.mDevice.getName(), ble.getLastRssi(), ble.getVariance());
                    }
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

    private void updateSQLite(String tableName, int distance, int rssi) {
        ContentValues values1 = new ContentValues();
//        values1.put("name", tableName);
        values1.put("distance", distance);
        values1.put("rssi", rssi);
        //参数依次是：表名，强行插入null值得数据列的列名，一行记录的数据
        sqLiteDatabase.insert(tableName, null, values1);
    }

    private void save() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SDFileHelper sdFileHelper = new SDFileHelper(context);
                for (BLE ble : mBLEs) {
                    try {
                        sdFileHelper.savaFileToSD(ble.mDevice.getName() + ".txt", ble.getRssiStr());
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
                tv_rssiCount.setText("已收集Rssi数据数量:");
                for (BLE ble : mBLEs) {
                    tv_rssiCount.append("\n" + ble.mDevice.getName()+":" + ble.rssiList.size());
                }
            }
        });

    }

}
