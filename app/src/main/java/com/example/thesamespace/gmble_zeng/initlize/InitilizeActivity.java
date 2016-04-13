package com.example.thesamespace.gmble_zeng.initlize;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.thesamespace.gmble_zeng.R;
import com.example.thesamespace.gmble_zeng.login.LogInActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by thesamespace on 2016/4/12.
 */
public class InitilizeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setFfullScreen();
        setContentView(R.layout.activity_initilize);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(InitilizeActivity.this, LogInActivity.class));
                finish();
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 1000);
    }

    private void setFfullScreen() {
        //不显示程序的标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //不显示系统的标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}
