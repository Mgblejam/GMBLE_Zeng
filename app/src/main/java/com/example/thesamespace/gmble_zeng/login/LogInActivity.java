package com.example.thesamespace.gmble_zeng.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.thesamespace.gmble_zeng.MainActivity;
import com.example.thesamespace.gmble_zeng.R;

import setting.SettingData;

/**
 * Created by thesamespace on 2016/4/12.
 */
public class LogInActivity extends Activity implements View.OnClickListener {
    SettingData settingData;
    private EditText edt_loginUserNmae;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFfullScreen();
        setContentView(R.layout.activity_login);
        settingData = (SettingData) getApplication();
        initView();
    }

    private void initView() {
        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        edt_loginUserNmae = (EditText) findViewById(R.id.edt_loginUserNmae);
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
            case R.id.btn_login:
                settingData.setUserNmae(edt_loginUserNmae.getText().toString());
                startActivity(new Intent(LogInActivity.this, MainActivity.class));
                finish();
                break;
        }
    }
}
