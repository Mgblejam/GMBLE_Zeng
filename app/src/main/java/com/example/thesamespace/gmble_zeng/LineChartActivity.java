package com.example.thesamespace.gmble_zeng;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by thesamespace on 2016/1/13.
 */
public class LineChartActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Button btn_start;
    private TextView tv_Legend;
    private TextView tv_maxBLEName;
    private LineChartView mLineChartView;
    private int[] colors = {ChartUtils.COLORS[0], ChartUtils.COLORS[1], ChartUtils.COLORS[2], ChartUtils.COLORS[3], ChartUtils.COLORS[4], Color.BLUE, Color.BLACK, Color.GREEN, Color.RED, Color.GRAY, Color.DKGRAY};
    private int colorIndex = 0;
    private boolean clearChartFlage = false;
    private LinearLayout linearLayout;
    private int checkboxID = 0;
    List<BLEData> bleDatas = new CopyOnWriteArrayList<>();
    String[] bleNames = new String[]{"SHELF01", "SHELF02", "SHELF03", "SHELF04"};
    private int counter = 0;
    static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Object lock = new Object();
    private MyLeScaner myLeScaner = new MyLeScaner() {
        @Override
        protected void mOnLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//            if (device.getName().equals("MYBLE")) {
//                String tempName = "";
//                switch (scanRecord[24]) {
//                    case 0:
//                        tempName = "-23dBm";
//                        break;
//                    case 1:
//                        tempName = "-6dBm";
//                        break;
//                    case 2:
//                        tempName = "0dBm";
//                        break;
//                    case 3:
//                        tempName = "4dBm";
//                        break;
//                }
//
//            }
            synchronized (lock) {
                addBLERssiLineToList(device.getName(), rssi);
            }
            for (final String bleName : bleNames) {
                if (bleName.equals(device.getName())) {
                    counter = 0;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_maxBLEName.setText(bleName);
                        }
                    });
                    break;
                }
            }
        }

        @Override
        protected void printLog(String str) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chartline);
        initView();
        isNoBLE();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myLeScaner.stopLeScan();
    }

    private void initView() {
        linearLayout = (LinearLayout) findViewById(R.id.layout_checkboxs);
        btn_start = (Button) findViewById(R.id.btn_startChart);
        Button btn_clearChart = (Button) findViewById(R.id.btn_clearChart);

        btn_start.setOnClickListener(this);
        btn_clearChart.setOnClickListener(this);

        tv_Legend = (TextView) findViewById(R.id.tv_Legend01);
        tv_maxBLEName = (TextView) findViewById(R.id.tv_maxBLEName);

        CheckBox ckb_rssiLine = (CheckBox) findViewById(R.id.ckb_rssiLine);
        CheckBox ckb_countLine = (CheckBox) findViewById(R.id.ckb_countLine);
        ckb_rssiLine.setOnCheckedChangeListener(this);
        ckb_countLine.setOnCheckedChangeListener(this);

        mLineChartView = (LineChartView) findViewById(R.id.linechartview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startChart:
                if (btn_start.getText().equals("Start")) {
                    btn_start.setText("Stop");
                    myLeScaner.startLeScan();
                    BLEData.addCountFlage = true;
                    addBLECountToList();
                } else {
                    btn_start.setText("Start");
                    myLeScaner.stopLeScan();
                    BLEData.addCountFlage = false;
                }
                break;
            case R.id.btn_clearChart:
                clearChartFlage = true;
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.ckb_rssiLine:
                BLEData.showRssiLineFlage = buttonView.isChecked();
                break;
            case R.id.ckb_countLine:
                BLEData.showCountLineFlage = buttonView.isChecked();
                break;
            default:
                for (BLEData bleData : bleDatas) {
                    if (bleData.bleName.equals(buttonView.getText().toString())) {
                        bleData.showFlage = buttonView.isChecked();
                        drawLine();
                        break;
                    }
                }
        }
    }

    private void addBLERssiLineToList(String bleName, int rssi) {
        if (clearChartFlage) {
            clearChartFlage = false;
            clearLines();
        }

        boolean exitFlage = false;
        {
            for (BLEData bleData : bleDatas) {
                if (bleData.bleName.equals(bleName)) {
                    exitFlage = true;
                    bleData.addRssiPoint(rssi);
                    bleData.addCount();
                    Log.d("locktest", "addCount");
                    System.out.println("addCount");
                    break;
                }
            }
        }

        if (!exitFlage) {
            bleDatas.add(new BLEData(bleName, rssi, colors[colorIndex]));
            colorIndex++;
            if (colorIndex == colors.length) {
                colorIndex = 0;
            }
            createCheckBox(bleName);
        }
        drawLine();
    }

    private void addBLECountToList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (BLEData.addCountFlage) {
                    try {
                        Thread.sleep(BLEData.cycle);
                        synchronized (lock) {
                            for (BLEData bleData : bleDatas) {
                                Log.d("locktest", "addCountPoint");
                                bleData.addmPointValues_count();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void clearLines() {
        for (BLEData bleData : bleDatas) {
            bleData.mPointValues_rssi.clear();
            bleData.mPointValues_count.clear();
        }
    }

    private void drawLine() {
        final LineChartData mData = new LineChartData();
        List<Line> mLines = new ArrayList<>();
        for (BLEData bleData : bleDatas) {
            if (bleData.showFlage) {
                if (BLEData.showRssiLineFlage) {
                    Line mLine = new Line(bleData.mPointValues_rssi);
                    mLine.setColor(bleData.lineColor);
                    mLine.setHasPoints(false);
                    mLine.setStrokeWidth(1);
                    mLines.add(mLine);
                }
//                if (BLEData.showCountLineFlage) {
//                    Line mLine;
//
//                    mLine = new Line(bleData.getmPointValues_count());
//                    Log.d("locktest", "drawLine");
//                    mLine.setColor(bleData.lineColor);
//                    mLine.setHasPoints(false);
//                    mLine.setStrokeWidth(1);
//                    mLines.add(mLine);
//                }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String str = "";
                for (BLEData bleData : bleDatas) {
                    if (bleData.showFlage) {
                        str += String.format("<font color=%d>%s</font><br>", bleData.lineColor, bleData.bleName);
                    }
                }
                tv_Legend.setText(Html.fromHtml(str));
            }
        });
    }

    private void createCheckBox(String checkBoxName) {
        final CheckBox checkBox = new CheckBox(this);
        checkBox.setText(checkBoxName);
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

    private void isNoBLE() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                        if (counter > 3) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_maxBLEName.setText("No BLE");
                                }
                            });
                        } else {
                            counter++;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();
    }

    private static class BLEData {
        private static int cycle = 1000;
        private static boolean addCountFlage = true;
        private static boolean showRssiLineFlage = true;
        private static boolean showCountLineFlage = true;
        private String bleName;
        private int lineColor;
        private int count;
        private boolean showFlage = true;
        private List<PointValue> mPointValues_rssi = new ArrayList<>();
        private List<PointValue> mPointValues_count = new ArrayList<>();
        private int index = 0;

        BLEData(String bleName, int rssi, int lineColor) {
            this.bleName = bleName;
            mPointValues_rssi.add(new PointValue(mPointValues_rssi.size(), rssi));
            this.lineColor = lineColor;
            count = 1;
        }

        private void addRssiPoint(int rssi) {
            mPointValues_rssi.add(new PointValue(mPointValues_rssi.size(), rssi));
        }

        private void addCountPoint() {
            mPointValues_count.add(new PointValue(index, count));
//            index++;
//            count = 0;

//            try {
//                readWriteLock.readLock().lock();
//            } finally {
//                readWriteLock.readLock().unlock();
//            }
//            try {
//                readWriteLock.writeLock().lock();
//
//            } finally {
//                readWriteLock.writeLock().unlock();
//            }
        }

        synchronized void addCount() {
            count++;
        }

        synchronized void addmPointValues_count() {
            this.mPointValues_count.add(new PointValue(mPointValues_count.size(), count));
//            count = 0;
        }

        synchronized List<PointValue> getmPointValues_count() {
            return this.mPointValues_count;
        }
    }
}