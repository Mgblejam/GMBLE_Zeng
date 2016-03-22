package com.example.thesamespace.gmble_zeng;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by thesamespace on 2016/2/1.
 */
public class SockeClient {
    public Socket socket = new Socket();
    private ListenCallBack listenCallBack;

    public interface ListenCallBack {
        void onListen(String receiveString);
    }

    public void startListen(ListenCallBack listenCallBack) {
        this.listenCallBack = listenCallBack;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket.isConnected()) {
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                        while (true) {
                            String receiveString = dataInputStream.readUTF();
                            if (receiveString == null) {
                                socket.close();
                                Log.d("error", "已断开连接");
                                break;
                            }
                            SockeClient.this.listenCallBack.onListen(receiveString);
                        }
                    } else {
                        Log.d("error", "未连接服务端");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendText(String sendText) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(sendText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectServer(final String serverIP, final int port, final String ID) {
        if (socket.isClosed())
            socket = new Socket();
        if (!socket.isConnected() ) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket.connect(new InetSocketAddress(serverIP, port), 5000);
                        sendText(ID + ",null");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("error", "Socket Had connectServer");
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
