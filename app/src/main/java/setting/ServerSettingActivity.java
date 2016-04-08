package setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.thesamespace.gmble_zeng.R;

/**
 * Created by thesamespace on 2016/4/8.
 */
public class ServerSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private SettingData settingData;
    EditText edt_serverIP;
    EditText edt_port;
    EditText edt_timeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Server Setting");
        setContentView(R.layout.activity_serversetting);
        settingData = (SettingData) getApplication();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        } else {
            System.out.println("actionBar为空");
        }
        init();
    }

    private void init() {
        edt_serverIP = (EditText) findViewById(R.id.edt_serverIP);
        edt_port = (EditText) findViewById(R.id.edt_port);
        edt_timeOut = (EditText) findViewById(R.id.edt_timeOut);
        readData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        applyData();
    }

    private void readData(){
        edt_serverIP.setText(settingData.getIP());
        edt_port.setText(String.valueOf(settingData.getPort()));
        edt_timeOut.setText(String.valueOf(settingData.getTimeOut()));
    }

    private void applyData(){
        settingData.setIP(edt_serverIP.getText().toString());
        settingData.setPort(Integer.parseInt(edt_port.getText().toString()));
        settingData.setTimeOut(Integer.parseInt(edt_timeOut.getText().toString()));
    }
}
