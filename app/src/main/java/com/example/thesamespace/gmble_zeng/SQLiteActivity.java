package com.example.thesamespace.gmble_zeng;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by thesamespace on 2016/1/18.
 */
public class SQLiteActivity extends Activity implements View.OnClickListener {
    private MySQLite mySQLite;
    private SQLiteDatabase sqLiteDatabase;
    private Button button;
    private Button button2;
    private TextView tv_rssiList;
    private StringBuilder sb;
    private String SDPATH;
    private boolean hasSD = false;
    private String fileName = "RssiTest.txt";
    private String FILESPATH;
    private SDFileHelper sdFileHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        init();
//        mySQLite = new MySQLite(SQLiteActivity.this, "BLEData.db", null, 2);
//        hasSD = Environment.getExternalStorageState().equals(
//                android.os.Environment.MEDIA_MOUNTED);
//        SDPATH = Environment.getExternalStorageDirectory().getPath();
//        FILESPATH = this.getFilesDir().getPath();
//        try {
//            File file = createSDFile(SDPATH + "//" + fileName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        sdFileHelper = new SDFileHelper(getApplicationContext());
    }

    private void init() {
        button = (Button) findViewById(R.id.btn_Start);
        button.setOnClickListener(this);

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);

        tv_rssiList = (TextView) findViewById(R.id.tv_rssiList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Start:
                showMsg("写入文件");
                try {
                    sdFileHelper.savaFileToSD("Test2","rssiTest.txt");
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                ContentValues values1 = new ContentValues();
////                values1.put("name", "BLE02");
//                values1.put("distance", 1);
//                values1.put("rssi", -59);
//                //参数依次是：表名，强行插入null值得数据列的列名，一行记录的数据
//                sqLiteDatabase.insert("MYBLE_00005", null, values1);
//                Toast.makeText(this, "插入完毕~", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button2:
                showMsg("删除文件");
//                sb = new StringBuilder();
                //参数依次是:表名，列名，where约束条件，where中占位符提供具体的值，指定group by的列，进一步约束
                //指定查询结果的排序方式
//                Cursor cursor = sqLiteDatabase.query("MYBLE_00005", null, null, null, null, null, null);
//                if (cursor.moveToFirst()) {
//                    do {
//                        int id = cursor.getInt(cursor.getColumnIndex("id"));
////                        String name = cursor.getString(cursor.getColumnIndex("name"));
//                        int distance = cursor.getInt(cursor.getColumnIndex("distance"));
//                        int rssi = cursor.getInt(cursor.getColumnIndex("rssi"));
//                        sb.append(String.format("ID:%d distance:%d rssi:%d \n", id, distance, rssi));
//                    } while (cursor.moveToNext());
//                }
//                cursor.close();
//                tv_rssiList.setText(sb.toString());
                break;
        }
    }

    public void showMsg(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_rssiList.append(str + "\n");
            }
        });
    }
}

class FileHelper {

    private Context mContext;

    public FileHelper() {
    }

    public FileHelper(Context mContext) {
        super();
        this.mContext = mContext;
    }

    /*
    * 这里定义的是一个文件保存的方法，写入到文件中，所以是输出流
    * */
    public void save(String filename, String filecontent) throws Exception {
        //这里我们使用私有模式,创建出来的文件只能被本应用访问,还会覆盖原文件哦
        FileOutputStream output = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
        output.write(filecontent.getBytes());  //将String字符串以字节流的形式写入到输出流中
        output.close();         //关闭输出流
    }


    /*
    * 这里定义的是文件读取的方法
    * */
    public String read(String filename) throws IOException {
        //打开文件输入流
        FileInputStream input = mContext.openFileInput(filename);
        byte[] temp = new byte[1024];
        StringBuilder sb = new StringBuilder("");
        int len = 0;
        //读取文件内容:
        while ((len = input.read(temp)) > 0) {
            sb.append(new String(temp, 0, len));
        }
        //关闭输入流
        input.close();
        return sb.toString();
    }

}

