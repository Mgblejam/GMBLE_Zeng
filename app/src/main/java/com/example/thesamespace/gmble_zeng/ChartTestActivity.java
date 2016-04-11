package com.example.thesamespace.gmble_zeng;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
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
    private Button btn_start;
    private TextView tv_Legend;
    private LineChartView mLineChartView;
    private int[] colors = {ChartUtils.COLORS[0], ChartUtils.COLORS[1], ChartUtils.COLORS[2], ChartUtils.COLORS[3], ChartUtils.COLORS[4], Color.BLUE, Color.BLACK, Color.GREEN, Color.RED, Color.GRAY, Color.DKGRAY};
    private int colorIndex = 0;
    private boolean clearChartFlage = false;
    private LinearLayout linearLayout;
    private int checkboxID = 0;
    List<BLERssiLine> bleRssiLines = new ArrayList<>();
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
            addBLERssiLineToList(device.getName(), rssi);
        }

        @Override
        protected void printLog(String str) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charttest);
        initView();
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

        mLineChartView = (LineChartView) findViewById(R.id.linechartview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_startChart:
                if (btn_start.getText().equals("Start")) {
                    btn_start.setText("Stop");
                    myLeScaner.startLeScan();
                } else {
                    btn_start.setText("Start");
                    myLeScaner.stopLeScan();
                }
                break;
            case R.id.btn_clearChart:
                clearChartFlage = true;
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (BLERssiLine bleRssiLine : bleRssiLines) {
            if (bleRssiLine.bleName.equals(buttonView.getText().toString())) {
                bleRssiLine.showFlage = buttonView.isChecked();
                drawLine();
                break;
            }
        }
    }

    private void addBLERssiLineToList(String bleName, int rssi) {
        if (clearChartFlage) {
            for (BLERssiLine bleRssiLine : bleRssiLines) {
                bleRssiLine.mPointValues_rssi.clear();
            }
            clearChartFlage = false;
        }
        boolean exitFlage = false;
        for (BLERssiLine bleRssiLine : bleRssiLines) {
            if (bleRssiLine.bleName.equals(bleName)) {
                exitFlage = true;
                bleRssiLine.mPointValues_rssi.add(new PointValue(bleRssiLine.mPointValues_rssi.size(), rssi));
                break;
            }
        }

        if (!exitFlage) {
            bleRssiLines.add(new BLERssiLine(bleName, rssi, colors[colorIndex]));
            colorIndex++;
            if (colorIndex == colors.length) {
                colorIndex = 0;
            }
            createCheckBox(bleName);
        }
        drawLine();
    }

    private void drawLine() {
        final LineChartData mData = new LineChartData();
        List<Line> mLines = new ArrayList<>();
        for (BLERssiLine bleRssiLine : bleRssiLines) {
            if (bleRssiLine.showFlage) {
                Line mLine = new Line(bleRssiLine.mPointValues_rssi);
                mLine.setColor(bleRssiLine.lineColor);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String str = "";
                for (BLERssiLine bleRssiLine : bleRssiLines) {
                    if (bleRssiLine.showFlage) {
                        str += String.format("<font color=%d>%s</font><br>", bleRssiLine.lineColor, bleRssiLine.bleName);
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

    class BLERssiLine {
        String bleName;
        List<PointValue> mPointValues_rssi = new ArrayList<>();
        int lineColor;
        boolean showFlage = true;

        BLERssiLine(String bleName, int rssi, int lineColor) {
            this.bleName = bleName;
            mPointValues_rssi.add(new PointValue(mPointValues_rssi.size(), rssi));
            this.lineColor = lineColor;
        }
    }
}
