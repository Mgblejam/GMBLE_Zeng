package com.example.thesamespace.gmble_zeng;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by thesamespace on 2016/1/18.
 */
public class SQLiteActivity extends Activity implements View.OnClickListener {
    private MySQLite mySQLite;
    private SQLiteDatabase sqLiteDatabase;
    private Button button;
    private Button button2;
    private TextView textView;
    private StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        init();
        mySQLite = new MySQLite(SQLiteActivity.this, "BLEData.db", null, 2);
    }

    private void init() {
        button = (Button) findViewById(R.id.btn_Start);
        button2 = (Button) findViewById(R.id.button2);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textview);
    }

    @Override
    public void onClick(View v) {
        sqLiteDatabase = mySQLite.getWritableDatabase();
        switch (v.getId()) {
            case R.id.btn_Start:
                ContentValues values1 = new ContentValues();
//                values1.put("name", "BLE02");
                values1.put("distance", 1);
                values1.put("rssi", -59);
                //参数依次是：表名，强行插入null值得数据列的列名，一行记录的数据
                sqLiteDatabase.insert("MYBLE_00005", null, values1);
                Toast.makeText(this, "插入完毕~", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button2:
                sb = new StringBuilder();
                //参数依次是:表名，列名，where约束条件，where中占位符提供具体的值，指定group by的列，进一步约束
                //指定查询结果的排序方式
                Cursor cursor = sqLiteDatabase.query("MYBLE_00005", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
//                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        int distance = cursor.getInt(cursor.getColumnIndex("distance"));
                        int rssi = cursor.getInt(cursor.getColumnIndex("rssi"));
                        sb.append(String.format("ID:%d distance:%d rssi:%d \n", id,  distance, rssi));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                textView.setText(sb.toString());
                break;
        }
    }
}
