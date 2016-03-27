package setting;

import android.app.Application;

/**
 * Created by thesamespace on 2016/3/25.
 */
public class SettingData extends Application {
    private String IP = "10.0.0.13";
    private int port = 9999;
    private int timeOut = 3000;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
}
