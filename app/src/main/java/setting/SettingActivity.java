package setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.thesamespace.gmble_zeng.R;

/**
 * Created by thesamespace on 2016/3/25.
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_ok;
    private Button btn_apply;
    private Button btn_cancel;
    private EditText edt_ip;
    private EditText edt_port;
    private EditText edt_timeOut;
    private SettingData settingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        settingData = (SettingData) getApplication();
        init();
    }

    private void init() {
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        btn_apply = (Button) findViewById(R.id.btn_apply);
        btn_apply.setOnClickListener(this);

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);

        edt_ip = (EditText) findViewById(R.id.edt_ip);
        edt_port = (EditText) findViewById(R.id.edt_port);
        edt_timeOut = (EditText) findViewById(R.id.edt_timeOut);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                applySetting();
                this.finish();
                break;
            case R.id.btn_apply:
                applySetting();
                break;
            case R.id.btn_cancel:
                this.finish();
                break;
        }
    }

    private void applySetting() {
        settingData.setIP(edt_ip.getText().toString());
        settingData.setPort(Integer.parseInt(edt_port.getText().toString()));
        settingData.setTimeOut(Integer.parseInt(edt_timeOut.getText().toString()));
    }
}
