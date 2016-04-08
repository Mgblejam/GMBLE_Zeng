package setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.thesamespace.gmble_zeng.R;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private SettingData settingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Setting");
        setContentView(R.layout.activity_setting);
        settingData = (SettingData) getApplication();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init() {
        TableRow tabrow_serverSetting = (TableRow) findViewById(R.id.tabrow_serverSetting);
        TableRow tabrow_bleScanSetting = (TableRow) findViewById(R.id.tabrow_bleScanSetting);

        tabrow_serverSetting.setOnClickListener(this);
        tabrow_bleScanSetting.setOnClickListener(this);
        readData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tabrow_serverSetting:
                startActivity(new Intent(SettingActivity.this, ServerSettingActivity.class));
                break;
            case R.id.tabrow_bleScanSetting:
                System.out.println("tabrow_bleScanSetting");
                break;
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

    private void readData() {
        TextView tv_serverIP = (TextView) findViewById(R.id.tv_serverIP);
        tv_serverIP.setText(settingData.getIP());
    }
}
