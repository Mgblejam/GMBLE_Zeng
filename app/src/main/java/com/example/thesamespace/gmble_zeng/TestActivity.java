package com.example.thesamespace.gmble_zeng;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by thesamespace on 2016/1/22.
 */
public class TestActivity extends Activity implements View.OnClickListener {
    private Button btn_rssiToDistance;
    private EditText edt_txPower;
    private EditText edt_n;
    private EditText edt_rssi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        init();
    }

    private void init() {
        btn_rssiToDistance = (Button) findViewById(R.id.btn_rssiToDistance);

        btn_rssiToDistance.setOnClickListener(this);

        edt_txPower = (EditText) findViewById(R.id.edt_txPower);
        edt_n = (EditText) findViewById(R.id.edt_n);
        edt_rssi = (EditText) findViewById(R.id.edt_rssi);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_rssiToDistance:
                float rssi = Integer.parseInt(edt_rssi.getText().toString());
                float txPower = Integer.parseInt(edt_txPower.getText().toString());
                double n = Float.parseFloat(edt_n.getText().toString());
                double distance = Math.pow(10, (rssi - txPower) / -10 * n);
                Toast.makeText(this, "" + distance, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
