package com.example.thesamespace.gmble_zeng;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import setting.SettingActivity;
import setting.SettingData;
import socket.SocketClient;


public class MainActivity extends Activity implements NavigationView.OnNavigationItemSelectedListener {
    private SettingData settingData;
    private TextView textView;
    private TextView tv_BLEList;
    private TextView tv_userName;
    private TextView tv_welcome;
    private TextView tv_chatMessage;
    private ImageView imageView;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BLE> mBLEs = new ArrayList<BLE>();
    private String tempStr = "";
    private int miniTimes = 20;
    private String lastMaxBLE;
    private String curMaxBLE;
    private float curMaxRssi = 0;
    private int biggerTimes;
    private String userName = "Marry";
    private SocketClient socketClient = new SocketClient() {
        @Override
        public void onListen(String receiveString) {
            String[] data = receiveString.split(",");
            switch (data[0]) {
                case "Login":
                    login(data[1], "");
                    break;
                case "Logout":
                    logout();
                    break;
            }
            ShowMsg(receiveString);
        }

        @Override
        public void onOffLine() {

        }

        @Override
        public void ShowMsg(final String logMessage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_chatMessage.append(logMessage + "\n");
                }
            });
        }
    };
    private String[] BLENames = new String[]{"MAGICWISE00005", "MAGICWISE00006", "MAGICWISE00007"};

    private List<BLEData> bleDataList = new ArrayList<>();
    private MyLeScaner myLeScaner = new MyLeScaner() {
        @Override
        protected void mOnLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            updateRssiAVG(device.getName(), rssi);
        }

        @Override
        protected void printLog(String str) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFfullScreen();
        setContentView(R.layout.activity_main);
        settingData = (SettingData) getApplication();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "No Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (mBluetoothAdapter.isEnabled() == false) {
            mBluetoothAdapter.enable();
        }
        init();
    }

    private void init() {
        textView = (TextView) findViewById(R.id.tv_rssiList);
        tv_BLEList = (TextView) findViewById(R.id.tv_bleList);
        tv_userName = (TextView) findViewById(R.id.tv_userName);
        tv_welcome = (TextView) findViewById(R.id.tv_welcome);
        tv_chatMessage = (TextView) findViewById(R.id.tv_chatMessage);
        imageView = (ImageView) findViewById(R.id.imageview);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setFfullScreen() {
        //不显示程序的标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //不显示系统的标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_start:
                if (item.getTitle().equals("Start")) {
                    item.setTitle("Stop");
                    start();
                } else {
                    item.setTitle("Start");
                    Stop();
                }
                break;
            case R.id.nav_debug:
                if (item.getTitle().equals("EnterDebugMode")) {
                    item.setTitle("ExitDebugMode");
                    textView.setVisibility(View.VISIBLE);
                    tv_BLEList.setVisibility(View.VISIBLE);
                    tv_chatMessage.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "EnterDebugMode", Toast.LENGTH_SHORT).show();
                } else {
                    item.setTitle("EnterDebugMode");
                    textView.setVisibility(View.INVISIBLE);
                    tv_BLEList.setVisibility(View.INVISIBLE);
                    tv_chatMessage.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "ExitDebugMode", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_chartTest:
                startActivity(new Intent(MainActivity.this, ChartTestActivity.class));
                break;
            case R.id.nav_collectRSSI:
                startActivity(new Intent(MainActivity.this, CollectRSSIActivity.class));
                break;
            case R.id.nav_setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            case R.id.nav_exit:
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device.getName().equals(BLENames[0]) || device.getName().equals(BLENames[1]) || device.getName().equals(BLENames[2])) {
                updateBLEList(device, rssi);
                Collections.sort(mBLEs);
                showBLEList();
                showNearestBLE();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

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

    private void updateRssiAVG(String bleName, int rssi) {
        boolean isExit = false;
        for (BLEData bleData : bleDataList) {
            if (bleData.bleName.equals(bleName)) {
                isExit = true;
                bleData.calculateRssiAVG(rssi);
                break;
            }
        }
        if (!isExit) {
            bleDataList.add(new BLEData(bleName, rssi));
        }
    }

    private void showBLEList() {
        tempStr = "";
        for (int i = 0; i < mBLEs.size(); i++) {
            if (i == 0) {
                tempStr += String.format("%s  %d avg:%.2f", mBLEs.get(i).mDevice.getName(), mBLEs.get(i).getLastRssi(), mBLEs.get(i).getRssiLastAVG());
            } else {
                tempStr += String.format("\n%s  %d avg:%.2f", mBLEs.get(i).mDevice.getName(), mBLEs.get(i).getLastRssi(), mBLEs.get(i).getRssiLastAVG());
            }
        }
        tv_BLEList.post(new Runnable() {
            @Override
            public void run() {
                tv_BLEList.setText(tempStr);
            }
        });
    }

    private void showNearestBLE() {
        if (mBLEs.get(0).rssiList.size() < 3) {
            return;
        }
        curMaxBLE = mBLEs.get(0).mDevice.getName();
        curMaxRssi = mBLEs.get(0).getRssiLastAVG();
        if (Math.abs(curMaxRssi - getLastMaxBLECurRssi(lastMaxBLE)) > 3) {
            biggerTimes++;
            if (biggerTimes >= miniTimes) {
                biggerTimes = 0;
                lastMaxBLE = mBLEs.get(0).mDevice.getName();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setPic(curMaxBLE, userName);
                        textView.setText(curMaxBLE);
                    }
                });
            }
        } else {
            biggerTimes = 0;
        }
    }

    private void setPic(String BLEname, String userName) {
        if (imageView.getScaleType() != ImageView.ScaleType.FIT_XY) {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        if (imageView.getAlpha() < 1.0f) {
            imageView.setAlpha(1.0f);
        }
        switch (userName) {
            case "Marry":
                switch (BLEname) {
                    case "MAGICWISE00005":
                        imageView.setImageResource(R.drawable.img11);
                        break;
                    case "MAGICWISE00006":
                        imageView.setImageResource(R.drawable.img12);
                        break;
                    case "MAGICWISE00007":
                        imageView.setImageResource(R.drawable.img13);
                        break;
                }
                break;
            case "Jack":
                switch (BLEname) {
                    case "MAGICWISE00001":
                        imageView.setImageResource(R.drawable.img21);
                        break;
                    case "MAGICWISE00002":
                        imageView.setImageResource(R.drawable.img22);
                        break;
                    case "MAGICWISE00003":
                        imageView.setImageResource(R.drawable.img23);
                        break;
                }
                break;
            case "Tom":
                switch (BLEname) {
                    case "MAGICWISE00001":
                        imageView.setImageResource(R.drawable.img31);
                        break;
                    case "MAGICWISE00002":
                        imageView.setImageResource(R.drawable.img32);
                        break;
                    case "MAGICWISE00003":
                        imageView.setImageResource(R.drawable.img33);
                        break;
                }
                break;
        }
    }

    private float getLastMaxBLECurRssi(String lastMaxBLE) {
        if (mBLEs.size() == 1) {
            return 0;
        }
        for (BLE ble : mBLEs) {
            if (ble.mDevice.getName().equals(lastMaxBLE)) {
                return ble.getRssiLastAVG();
            }
        }
        return 0;
    }

    private void login(final String userName, String type) {
        new Thread() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String type = "";
                        switch (userName) {
                            case "Marry":
                                type = "(woman)";
                                break;
                            case "Jack":
                                type = "(oldman)";
                                break;
                            case "Tom":
                                type = "(child)";
                                break;
                        }
                        tv_userName.setText("User:" + userName + type);
                        tv_welcome.setVisibility(View.VISIBLE);
                        tv_welcome.setText("Welcome  " + userName);
                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Animation alphaAnimation = new AlphaAnimation(1.0f, 0f);
                        alphaAnimation.setDuration(1000);
                        tv_welcome.startAnimation(alphaAnimation);
                        tv_welcome.setVisibility(View.GONE);
                    }
                });
            }
        }.start();
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        myLeScaner.startLeScan();
    }

    private void logout() {
        socketClient.disConnect();
        new Thread() {
            @Override
            public void run() {
                biggerTimes = 0;
                lastMaxBLE = "";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_userName.setText("User: No User");
                        tv_welcome.setVisibility(View.VISIBLE);
                        tv_welcome.setText("ByeBye  " + userName);
                        imageView.setImageResource(R.drawable.magicwiselogo);
                        imageView.setAlpha(0.3f);
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Animation alphaAnimation = new AlphaAnimation(1.0f, 0f);
                        alphaAnimation.setDuration(1000);
                        tv_welcome.startAnimation(alphaAnimation);
                        tv_welcome.setVisibility(View.GONE);
                    }
                });
            }
        }.start();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        myLeScaner.stopLeScan();
    }

    private void start() {
        printLog("连接服务器");
        socketClient.connect(settingData.getIP(), settingData.getPort());
    }

    private void Stop() {
        socketClient.disConnect();
        logout();
    }

    private void printLog(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_chatMessage.append(str + "\n");
            }
        });
    }

    class BLEData {
        String bleName;
        List<Integer> rssiList = new ArrayList<>();
        List<Float> rssiAVGList = new ArrayList<>();

        BLEData(String bleName, int rssi) {
            this.bleName = bleName;
            rssiList.add(rssi);
            calculateRssiAVG(rssi);
        }

        void calculateRssiAVG(int rssi) {
            float rssiAVG = (rssi - rssiAVGList.get(rssiAVGList.size() - 1)) * 0.1f;
            rssiAVGList.add(rssiAVG);
        }
    }
}