package com.example.thesamespace.gmble_zeng;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends Activity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private Button btn_Start;
    private Button btn_SetMiniRssi;
    private EditText edt_miniRssi;
    private TextView textView;
    private TextView tv_BLEList;
    private TextView tv_userName;
    private TextView tv_welcome;
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

    private Handler myHandler;
    private volatile ServerSocket server = null;
    private static final int PORT = 9999;
    private ExecutorService mExecutorService = null; // 线程池
    private volatile boolean flag = true;// 线程标志位
    private List<Socket> mList = new ArrayList<Socket>();
    private static final int LOGIN = 1;
    private static final int LOGOUT = 2;
    //    private ServerThread serverThread;
    private SockeClient sockeClient = new SockeClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFfullScreen();
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "No Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (mBluetoothAdapter.isEnabled() == false) {
            mBluetoothAdapter.enable();
        }
        init();
//        myHandler = new Handler() {
//            @SuppressLint("HandlerLeak")
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case LOGIN:
//                        userName = msg.obj.toString();
//                        Log.d("test", userName);
//                        login();
//                        mBluetoothAdapter.startLeScan(mLeScanCallback);
//                        break;
//                    case LOGOUT:
//                        logout();
//                        Toast.makeText(MainActivity.this, "LOGOUT", Toast.LENGTH_SHORT).show();
//                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                        mBLEs.clear();
//                        break;
//                }
//            }
//        };
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menu.add(1, ChartTest, 1, "ChartTest");
//        menu.add(1, MapTest, 2, "MapTest");
//        menu.add(1, SQLiteTest, 3, "SQLiteTest");
//        menu.add(1, FunctionTest, 4, "FunctionTest");
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up btn_Start, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        switch (item.getItemId()) {
//            case ChartTest:
//                startActivity(new Intent(MainActivity.this, ChartTestActivity.class));
//                break;
//            case MapTest:
//                startActivity(new Intent(MainActivity.this, MapActivity.class));
//                break;
//            case SQLiteTest:
//                startActivity(new Intent(MainActivity.this, SQLiteActivity.class));
//                break;
//            case FunctionTest:
//                startActivity(new Intent(MainActivity.this, TestActivity.class));
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void init() {
        btn_Start = (Button) findViewById(R.id.btn_Start);
        btn_SetMiniRssi = (Button) findViewById(R.id.btn_SetMiniTimes);

        btn_Start.setOnClickListener(this);
        btn_SetMiniRssi.setOnClickListener(this);

        edt_miniRssi = (EditText) findViewById(R.id.edt_miniTimes);

        textView = (TextView) findViewById(R.id.textview);
        tv_BLEList = (TextView) findViewById(R.id.textview2);
        tv_userName = (TextView) findViewById(R.id.tv_userName);
        tv_welcome = (TextView) findViewById(R.id.tv_welcome);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Start:
                if (btn_Start.getText().equals("Start")) {
                    btn_Start.setText("Stop");
                    Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
                } else {
                    btn_Start.setText("Start");
                    Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_SetMiniTimes:
                miniTimes = Integer.parseInt(edt_miniRssi.getText().toString());
                Toast.makeText(this, "Setting miniTimes OK", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_start:
                if (item.getTitle().equals("Start")) {
                    item.setTitle("Stop");
                    start();
                    Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
                } else {
                    item.setTitle("Start");
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    serverThread.stopServer();
                    Stop();
                    Toast.makeText(MainActivity.this, "Stop", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_debug:
                if (item.getTitle().equals("EnterDebugMode")) {
                    item.setTitle("ExitDebugMode");
                    textView.setVisibility(View.VISIBLE);
                    tv_BLEList.setVisibility(View.VISIBLE);
                    btn_SetMiniRssi.setVisibility(View.VISIBLE);
                    edt_miniRssi.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "EnterDebugMode", Toast.LENGTH_SHORT).show();
                } else {
                    item.setTitle("EnterDebugMode");
                    textView.setVisibility(View.INVISIBLE);
                    tv_BLEList.setVisibility(View.INVISIBLE);
                    btn_SetMiniRssi.setVisibility(View.INVISIBLE);
                    edt_miniRssi.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "ExitDebugMode", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_chartTest:
                startActivity(new Intent(MainActivity.this, ChartTestActivity.class));
                break;
            /*case R.id.nav_mapTest:
                startActivity(new Intent(MainActivity.this, MapActivity.class));
                break;*/
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
            if (device.getName().equals("MAGICWISE00001") || device.getName().equals("MAGICWISE00002") || device.getName().equals("MAGICWISE00003")) {
                updateBLEList(device, rssi);
                Collections.sort(mBLEs);
                showBLEList();
                showNearestBLE();
            }
        }
    };

    public SockeClient.ListenCallBack listenCallBack = new SockeClient.ListenCallBack() {
        @Override
        public void onListen(String receiveString) {
            Log.d("receiveString", receiveString);
            String[] data = receiveString.split(",");
            switch (data[0]) {
                case "Login":
                    login(data[1].toString(), data[2]);
                    break;
                case "Logout":
                    logout();
                    break;
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
                    case "MAGICWISE00001":
                        imageView.setImageResource(R.drawable.img11);
                        break;
                    case "MAGICWISE00002":
                        imageView.setImageResource(R.drawable.img12);
                        break;
                    case "MAGICWISE00003":
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
    }

    private void logout() {
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
    }

    private void start() {
        sockeClient.connectServer("10.0.0.30", 9999, "01");
        sockeClient.startListen(listenCallBack);
//        serverThread = new ServerThread();
//        flag = true;
//        serverThread.start();
//         login();
//         mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    private void Stop() {
        logout();
        sockeClient.disconnect();
    }

    class ServerThread extends Thread {
        public void stopServer() {
            try {
                if (server != null) {
                    server.close();
                    System.out.println("close task successed");
                }
            } catch (IOException e) {
                System.out.println("close task failded");
            }
        }

        public void run() {
            try {
                server = new ServerSocket(PORT);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                System.out.println("S2: Error");
                e1.printStackTrace();
            }
            mExecutorService = Executors.newCachedThreadPool(); // 创建一个线程池
            System.out.println("Start server...");
            Socket client = null;
            while (flag) {
                try {
                    client = server.accept();
                    System.out.println("accept");
                    // 把客户端放入客户端集合中
                    mList.add(client);
                    mExecutorService.execute(new Service(client)); // 启动一个新的线程来处理连接
                } catch (IOException e) {
                    System.out.println("S1: Error");
                    e.printStackTrace();
                }
            }

        }
    }

    // 处理与client对话的线程
    class Service implements Runnable {
        DataInputStream in;
        DataOutputStream out;
        Socket socket;

        public Service(Socket socket) {
            try {
                this.socket = socket;
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }

        public void run() {
            while (true) {
                try {
                    String getdata = getRequestData();
                    System.out.println("get:  " + getdata);
                    String[] r_data = getdata.split("\\|");
                    Message msgLocal = new Message();
                    if (r_data[0].equals("LI")) {
                        msgLocal.what = LOGIN;
                        msgLocal.obj = r_data[1];
                        System.out.println(msgLocal.obj.toString());
                        System.out.println(getdata);
                        myHandler.sendMessage(msgLocal);
                        break;

                    } else if (r_data[0].equals("LO")) {
                        msgLocal.what = LOGOUT;
                        msgLocal.obj = r_data[1];
                        System.out.println(msgLocal.obj.toString());
                        System.out.println(getdata);
                        myHandler.sendMessage(msgLocal);
                        break;
                    } else if (getdata.equals("ok\r\n")) {
                        mList.remove(socket);
                        in.close();
                        socket.close();
                        break;
                    }

                } catch (IOException e) {
                    System.out.println("close");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        private String getRequestData() throws IOException {
            byte[] buffer = new byte[1024];
            String getdata = "";
            int in_data = in.read(buffer);

            for (byte b : buffer) {
                if (b != 0) {
                    getdata += (char) b;
                }
            }
            return getdata;

        }

    }
}

