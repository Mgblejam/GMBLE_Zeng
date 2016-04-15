package setting;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by thesamespace on 2016/3/25.
 */
public class SettingData extends Application {
    private String userNmae;
    private SharedPreferences config;
    private String IP = "127.0.0.1";
    private int port = 9999;
    private int timeOut = 3000;
    private int miniBigerTimes = 10;

    @Override
    public void onCreate() {
        super.onCreate();
        config = getSharedPreferences("config", MODE_PRIVATE);
        readConfig();
    }

    private void readConfig() {
        IP = config.getString("IP", "127.0.0.1");
        port = config.getInt("port", 9999);
        timeOut = config.getInt("timeOut", 1000);
    }

    public void saveConfig() {
        SharedPreferences.Editor editor = config.edit();
        editor.putString("IP", IP);
        editor.putInt("port", port);
        editor.putInt("timeOut", timeOut);
        editor.apply();
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

    public int getMiniBigerTimes() {
        return miniBigerTimes;
    }

    public void setMiniBigerTimes(int miniBigerTimes) {
        this.miniBigerTimes = miniBigerTimes;
    }

    public String getUserNmae() {
        return userNmae;
    }

    public void setUserNmae(String userNmae) {
        this.userNmae = userNmae;
    }
}
