package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by thesamespace on 2016/2/1.
 */

public abstract class SocketClient {

    private boolean checkOnLineThreadFlage = true;
    private boolean ListenThreadFlage = true;

    public SocketClient() {
        init();
    }

    private void init() {

    }

    protected Socket socket;

    public void connect(final String IP, final int port) {
        if (socket != null && socket.isConnected()) {
            printLog("Already connected!");
            return;
        }

        printLog("Connecting:" + IP + " " + port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress(IP, port), 1000);
                } catch (IOException e) {
                    e.printStackTrace();
                    printLog(e.getMessage());
                    return;
                }

                if (socket.isConnected()) {
                    printLog("Connect successfully！");
                } else {
                    printLog("Connect failed！");
                    return;
                }

                checkOnLineThread();
                listenThread();
            }
        }).start();
    }

    public void send(String sendStr) {
        if (socket == null || !socket.isConnected()) {
            printLog("Not connected！");
            return;
        }
        if (sendStr.equals("")) {
            printLog("Send content can not be empty!");
            return;
        }

        OutputStream os;
        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            printLog(e.getMessage());
            return;
        }
        try {
            os.write(sendStr.getBytes("gb2312"));
        } catch (IOException e) {
            e.printStackTrace();
            printLog(e.getMessage());
            return;
        }
        printLog(sendStr);
    }

    public void disConnect() {
        if (socket == null || !socket.isConnected()) {
            printLog("Not connected!");
            return;
        }
        try {
            checkOnLineThreadFlage = false;
            socket.close();
            socket = null;
            printLog("Disconnect successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            printLog(e.getMessage());
            return;
        }
    }

    private void listenThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream;
                if (socket == null) {
                    return;
                }

                try {
                    inputStream = socket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                    printLog(e.getMessage());
                    return;
                }

                int length;
                while (ListenThreadFlage) {
//                    do {
//                        try {
//                            length = inputStream.read();
//                            System.out.println("接收中。。。。" + length);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            return;
//                        }
//                    } while (length == 0);
                    byte[] bytes = new byte[1024];
                    try {
                        int a = inputStream.read(bytes);
                        System.out.println("接收中。。。。" + a);
                    } catch (IOException e) {
                        e.printStackTrace();
                        printLog(e.getMessage());
                        return;
                    }

                    try {
                        onListen(bytes);
                        onListen(new String(bytes, "gb2312"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        printLog(e.getMessage());
                        return;
                    }
                }
            }
        }).start();
    }

    private void checkOnLineThread() {
        checkOnLineThreadFlage = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (checkOnLineThreadFlage) {
                    if (!isOnLine()) {
                        socket = null;
                        printLog("offline...");
                        onOffLine();
                        return;
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }).start();
    }

    private boolean isOnLine() {
        if (socket == null) {
            return false;
        }
        try {
            socket.sendUrgentData(0xFF);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public abstract void onListen(final String receiveString);

    public abstract void onListen(final byte[] bytes);

    public abstract void onOffLine();

    public abstract void printLog(final String logMessage);
}