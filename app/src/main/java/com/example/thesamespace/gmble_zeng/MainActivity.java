package com.example.thesamespace.gmble_zeng;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final private int ChartTest = 110;
    final private int MapTest = 111;

    private Button button;
    private Button button2;
    private Button button3;
    private EditText editText;
    private TextView textView;
    private TextView textView2;
    private ImageView imageView;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BLE> mBLEs = new ArrayList<BLE>();
    private String tempStr = "";
    private int miniRssi = -66;
    private String maxNameLast = "";
    private String maxName = "";
    private int maxTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setFfullScreen();
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "No Bluetooth", Toast.LENGTH_SHORT).show();
        }
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(1, ChartTest, 1, "ChartTest");
        menu.add(1, MapTest, 2, "MapTest");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case ChartTest:
                startActivity(new Intent(MainActivity.this, ChartTestActivity.class));
                break;
            case MapTest:
                startActivity(new Intent(MainActivity.this, MapActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        editText = (EditText) findViewById(R.id.edittext);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textview);
        textView2 = (TextView) findViewById(R.id.textview2);
        imageView = (ImageView) findViewById(R.id.imageview);
    }

    private void setFfullScreen() {
        //不显示程序的标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //不显示系统的标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:

                mBluetoothAdapter.startLeScan(mLeScanCallback);
                Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
                button.setEnabled(false);
                break;
            case R.id.button2:
                Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                button.setEnabled(true);
                break;
            case R.id.button3:
                miniRssi = Integer.parseInt(editText.getText().toString());
                Toast.makeText(this, "Setting miniRssi OK", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            updateBLEList(device, rssi);
            Collections.sort(mBLEs);
            showBLEList();
            showNearestBLE();
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
            mBLEs.add(mBle);
        }
    }

    private void showBLEList() {
        tempStr = "";
        for (int i = 0; i < mBLEs.size(); i++) {
            tempStr += mBLEs.get(i).mDevice.getName() + "  " + mBLEs.get(i).getLastRssi() + "\n";
        }
        textView2.post(new Runnable() {
            @Override
            public void run() {
                textView2.setText(tempStr);
            }
        });
    }

    private void showNearestBLE() {
        maxName = mBLEs.get(0).mDevice.getName();
        if (maxNameLast.equals(maxName)) {
            maxTimes++;
        } else {
            maxNameLast = maxName;
            maxTimes = 0;
        }

        if (maxTimes >= 3) {
            if (mBLEs.get(0).rssiSum > miniRssi) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setPic(mBLEs.get(0).mDevice.getName());
                        textView.setText(mBLEs.get(0).mDevice.getName());
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("No Beacon");
                    }
                });
            }
        }
    }

    private void setPic(String name) {
        if (name.equals("MYBLE01")) {
            imageView.setImageResource(R.drawable.img1);
        } else if (name.equals("MYBLE02")) {
            imageView.setImageResource(R.drawable.img2);
        } else if (name.equals("MYBLE03")) {
            imageView.setImageResource(R.drawable.img3);
        } else if (name.equals("MYBLE04")) {
            imageView.setImageResource(R.drawable.img4);
        } else if (name.equals("MYBLE_00005")) {
            imageView.setImageResource(R.drawable.img5);
        } else if (name.equals("MYBLE_00006")) {
            imageView.setImageResource(R.drawable.img6);
        } else if (name.equals("MYBLE_00007")) {
            imageView.setImageResource(R.drawable.img7);
        }
    }

}
