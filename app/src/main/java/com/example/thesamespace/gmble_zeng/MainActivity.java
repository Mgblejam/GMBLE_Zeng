package com.example.thesamespace.gmble_zeng;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import setting.SettingActivity;
import setting.SettingData;
import socket.SocketClient;
import socket.SocketTestActivity;


public class MainActivity extends Activity implements NavigationView.OnNavigationItemSelectedListener {
    private SettingData settingData;

    private ImageView img_mainBackground;
    private ImageView img_userHead;
    private TextView tv_userNmae;
    private TextView tv_mainLog;

    private List<BLEData> bleDataList = new ArrayList<>();
    private String tempStr = "";
    private String lastMaxBLE;
    private String curMaxBLE;
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
            printLog(receiveString);
        }

        @Override
        public void onListen(byte[] bytes) {

        }

        @Override
        public void onOffLine() {

        }

        @Override
        public void printLog(final String logMessage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    };
    private String[] BLENames = new String[]{"MAGICWISE00005", "MAGICWISE00006", "MAGICWISE00007"};
    private MyLeScaner myLeScaner = new MyLeScaner() {
        @Override
        protected void mOnLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device.getName().equals(BLENames[0]) || device.getName().equals(BLENames[1]) || device.getName().equals(BLENames[2])) {
                addBLEData(device.getName(), rssi);
                Collections.sort(bleDataList);
                showNearestBLE();
                printLog(device.getName());
            }
        }

        @Override
        protected void printLog(final String str) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_mainLog.append("\n" + str);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFfullScreen();
        setContentView(R.layout.activity_main);
        settingData = (SettingData) getApplication();
        initView();
    }

    private void initView() {
        img_mainBackground = (ImageView) findViewById(R.id.img_mainBackground);
        img_userHead = (ImageView) findViewById(R.id.img_userHead);
        tv_userNmae = (TextView) findViewById(R.id.tv_userName);
        tv_mainLog = (TextView) findViewById(R.id.tv_mainLog);

        if (!settingData.getUserNmae().equals("")) {
            tv_userNmae.setText(settingData.getUserNmae());
        }

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
                break;
            case R.id.nav_chartTest:
                startActivity(new Intent(MainActivity.this, LineChartActivity.class));
                break;
            case R.id.nav_collectRSSI:
                startActivity(new Intent(MainActivity.this, CollectRSSIActivity.class));
                break;
            case R.id.nav_socketTest:
                startActivity(new Intent(MainActivity.this, SocketTestActivity.class));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myLeScaner.stopLeScan();
    }

    private void addBLEData(String bleName, int rssi) {
        boolean isExit = false;
        for (BLEData bleData : bleDataList) {
            if (bleData.bleName.equals(bleName)) {
                isExit = true;
                bleData.addRssiAVG(rssi);
                break;
            }
        }
        if (!isExit) {
            bleDataList.add(new BLEData(bleName, rssi));
        }
    }

//    private void showBLEList() {
//        tempStr = "";
//        for (int i = 0; i < bleDataList.size(); i++) {
//            if (i == 0) {
//                tempStr += String.format("%s  %d avg:%.2f", bleDataList.get(i).mDevice.getName(), bleDataList.get(i).getLastRssi(), bleDataList.get(i).getRssiLastAVG());
//            } else {
//                tempStr += String.format("\n%s  %d avg:%.2f", bleDataList.get(i).mDevice.getName(), bleDataList.get(i).getLastRssi(), bleDataList.get(i).getRssiLastAVG());
//            }
//        }
//    }

    private void showNearestBLE() {
//        if (bleDataList.get(0).rssiAVGList.size() < 2) {
//            return;
//        }
        curMaxBLE = bleDataList.get(0).bleName;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setPic(curMaxBLE, userName);
            }
        });
//        float curMaxRssi = bleDataList.get(0).lastRssiAVG;
//
//        if (Math.abs(curMaxRssi - getLastMaxBLECurRssi(lastMaxBLE)) > 3) {
//            biggerTimes++;
//            if (biggerTimes >= settingData.getMiniBigerTimes()) {
//                biggerTimes = 0;
//                lastMaxBLE = bleDataList.get(0).bleName;
//            }
//        } else {
//            biggerTimes = 0;
//        }
    }

    private void setPic(String BLEname, String userName) {
        if (img_mainBackground.getScaleType() != ImageView.ScaleType.FIT_XY) {
            img_mainBackground.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        if (img_mainBackground.getAlpha() < 1.0f) {
            img_mainBackground.setAlpha(1.0f);
        }
        switch (userName) {
            case "Marry":
                switch (BLEname) {
                    case "MAGICWISE00005":
                        img_mainBackground.setImageResource(R.drawable.img11);
                        break;
                    case "MAGICWISE00006":
                        img_mainBackground.setImageResource(R.drawable.img12);
                        break;
                    case "MAGICWISE00007":
                        img_mainBackground.setImageResource(R.drawable.img13);
                        break;
                }
                break;
            case "Jack":
                switch (BLEname) {
                    case "MAGICWISE00001":
                        img_mainBackground.setImageResource(R.drawable.img21);
                        break;
                    case "MAGICWISE00002":
                        img_mainBackground.setImageResource(R.drawable.img22);
                        break;
                    case "MAGICWISE00003":
                        img_mainBackground.setImageResource(R.drawable.img23);
                        break;
                }
                break;
            case "Tom":
                switch (BLEname) {
                    case "MAGICWISE00001":
                        img_mainBackground.setImageResource(R.drawable.img31);
                        break;
                    case "MAGICWISE00002":
                        img_mainBackground.setImageResource(R.drawable.img32);
                        break;
                    case "MAGICWISE00003":
                        img_mainBackground.setImageResource(R.drawable.img33);
                        break;
                }
                break;
        }
    }

    private float getLastMaxBLECurRssi(String lastMaxBLE) {
        if (bleDataList.size() == 1) {
            return 0;
        }
        for (BLEData bleData : bleDataList) {
            if (bleData.bleName.equals(lastMaxBLE)) {
                return bleData.lastRssiAVG;
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
//                        tv_welcome.startAnimation(alphaAnimation);
//                        tv_welcome.setVisibility(View.GONE);
                    }
                });
            }
        }.start();
        myLeScaner.startLeScan();
    }

    private void logout() {
//        socketClient.disConnect();
        new Thread() {
            @Override
            public void run() {
                biggerTimes = 0;
                lastMaxBLE = "";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        img_mainBackground.setImageResource(R.drawable.magicwiselogo);
                        img_mainBackground.setAlpha(0.3f);
                        img_mainBackground.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
//                        tv_welcome.startAnimation(alphaAnimation);
//                        tv_welcome.setVisibility(View.GONE);
                    }
                });
            }
        }.start();
        myLeScaner.stopLeScan();
    }

    private void start() {
        login("Marry", "");
//        socketClient.connect(settingData.getIP(), settingData.getPort());
    }

    private void Stop() {
        logout();
    }

    private class BLEData implements Comparable<Object> {
        String bleName;
        float lastRssiAVG = 0;
        List<Integer> rssiList = new ArrayList<>();
        List<Float> rssiAVGList = new ArrayList<>();

        BLEData(String bleName, int rssi) {
            this.bleName = bleName;
            addRssi(rssi);
            addRssiAVG(rssi);
        }

        void addRssi(int rssi) {
            rssiList.add(rssi);

        }

        void addRssiAVG(int rssi) {
            lastRssiAVG = (rssi - lastRssiAVG) * 0.1f;
            rssiAVGList.add(lastRssiAVG);
        }

        @Override
        public int compareTo(@NonNull Object another) {
            BLEData bleData = (BLEData) another;
            Float i = bleData.lastRssiAVG;
            return i.compareTo(this.lastRssiAVG);
        }
    }
}