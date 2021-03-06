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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import setting.SettingActivity;
import setting.SettingData;
import socket.SocketClient;
import socket.SocketTestActivity;


public class MainActivity extends Activity implements NavigationView.OnNavigationItemSelectedListener, ViewSwitcher.ViewFactory {
    private SettingData settingData;

    private ImageSwitcher img_mainBackground;
    private ImageView img_userHead;
    private TextView tv_userNmae;
    private TextView tv_mainLog;
    private TextView tv_curMaxBLE;

    private List<BLEData> bleDataList = new ArrayList<>();
    private String tempStr = "";
    private String lastMaxBLE = "";
    private String curMaxBLE = "";
    private int biggerTimes;
    private String userName = "Marry";
    private boolean waitFlage = false;
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
    private String[] ourBLEList = new String[]{"SHELF01", "SHELF02", "SHELF03", "SHELF04"};
    private String[] ourBLEList2 = new String[]{"MAGICWISE00005", "MAGICWISE00006", "MAGICWISE00007"};
    private HashMap<String, String> nameList = new HashMap();
    private MyLeScaner myLeScaner = new MyLeScaner() {
        @Override
        protected void mOnLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            boolean isOurBLE = false;
            for (String bleName : ourBLEList2) {
                if (bleName.equals(device.getName())) {
                    isOurBLE = true;
                    break;
                }
            }

            if (!isOurBLE) {
                return;
            }

            addBLEData(device.getName(), rssi);
            showNearestBLE();
        }

        @Override
        protected void printLog(final String str) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_mainLog.append(str);
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
        nameList.put("SHELF01", "Zone 01");
        nameList.put("SHELF02", "Zone 02");
        nameList.put("SHELF03", "Zone 03");
        nameList.put("SHELF04", "Zone 04");
        nameList.put("MAGICWISE00005", "Zone 05");
        nameList.put("MAGICWISE00006", "Zone 06");
        nameList.put("MAGICWISE00007", "Zone 07");
    }

    private void initView() {
        img_mainBackground = (ImageSwitcher) findViewById(R.id.img_mainBackground);
        img_mainBackground.setFactory(this);
        img_userHead = (ImageView) findViewById(R.id.img_userHead);
        tv_userNmae = (TextView) findViewById(R.id.tv_userName);
        tv_mainLog = (TextView) findViewById(R.id.tv_mainLog);
        tv_curMaxBLE = (TextView) findViewById(R.id.tv_curMaxBLE);

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
                bleData.updateRssiAVGList(rssi);
                break;
            }
        }
        if (!isExit) {
            bleDataList.add(new BLEData(bleName, rssi));
        }
    }

    private String getBLEListStr() {
        tempStr = "";
        for (BLEData bleData : bleDataList) {
            tempStr += String.format("\n%s %f", bleData.bleName, bleData.lastRssiAVG);
        }
        return tempStr;
    }

    private void showNearestBLE() {
        if (bleDataList.size() < 3) {
            return;
        }
        Collections.sort(bleDataList);
        curMaxBLE = bleDataList.get(0).bleName;

        if (lastMaxBLE.equals("")) {
            lastMaxBLE = curMaxBLE;
        }

        float curMaxRssi = bleDataList.get(0).lastRssiAVG;
        if (Math.abs(curMaxRssi - getLastMaxBLECurRssi(lastMaxBLE)) > 3) {
            biggerTimes++;
            if (biggerTimes >= settingData.getMiniBigerTimes()) {
                biggerTimes = 0;
                curMaxBLE = bleDataList.get(0).bleName;
            }
        } else {
            biggerTimes = 0;
        }

        if (!waitFlage) {
            waitFlage = true;
            if (!curMaxBLE.equals(lastMaxBLE)) {
                lastMaxBLE = curMaxBLE;
                setPicThread(lastMaxBLE);
            }
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    waitFlage = false;
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 2000);
        }
    }

    private void setPicThread(final String blename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_curMaxBLE.setText(nameList.get(blename));
                        Animation alphaAnimationIn = new AlphaAnimation(0f, 1.0f);
                        alphaAnimationIn.setDuration(2000);
                        Animation alphaAnimationOut = new AlphaAnimation(1.0f, 0f);
                        alphaAnimationOut.setDuration(2000);
                        img_mainBackground.setInAnimation(alphaAnimationIn);
                        img_mainBackground.setOutAnimation(alphaAnimationOut);
                        setPic(blename, userName);
                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setPic(String BLEname, String userName) {
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
                    case "SHELF01":
                        img_mainBackground.setImageResource(R.drawable.img11);
                        break;
                    case "SHELF02":
                        img_mainBackground.setImageResource(R.drawable.img12);
                        break;
                    case "SHELF03":
                        img_mainBackground.setImageResource(R.drawable.img13);
                        break;
                    case "SHELF04":
                        img_mainBackground.setImageResource(R.drawable.img14);
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

    @Override
    public View makeView() {
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    private class BLEData implements Comparable<Object> {
        String bleName;
        float lastRssiAVG = 0;
        int count = 0;
        List<Integer> rssiList = new ArrayList<>();
        List<Float> rssiAVGList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();

        BLEData(String bleName, int rssi) {
            this.bleName = bleName;
            updateRssiList(rssi);
            updateRssiAVGList(rssi);
        }

        void updateRssiList(int rssi) {
            rssiList.add(rssi);
        }

        void updateRssiAVGList(int rssi) {
            lastRssiAVG += (rssi - lastRssiAVG) * 0.1f;
            rssiAVGList.add(lastRssiAVG);
        }

        void updateCountList() {
            countList.add(count);
        }

        @Override
        public int compareTo(@NonNull Object another) {
            BLEData bleData = (BLEData) another;
            Float i = bleData.lastRssiAVG;
            return i.compareTo(this.lastRssiAVG);
        }
    }
}