package com.example.thesamespace.gmble_zeng;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by thesamespace on 2016/3/24.
 * 需要：<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 */
public class SDFileHelper {
    private Context context;

    public SDFileHelper() {
    }

    public SDFileHelper(Context context) {
        super();
        this.context = context;
    }

    public void savaFileToSD(String filename, String filecontent) throws Exception {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + filename;
            FileOutputStream output = new FileOutputStream(filename);
            output.write(filecontent.getBytes());
            output.flush();
            output.close();
        } else Toast.makeText(context, "SD卡不存在或者不可读写", Toast.LENGTH_SHORT).show();
    }

    public String readFromSD(String filename) throws IOException {
        StringBuilder sb = new StringBuilder("");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + filename;
            FileInputStream input = new FileInputStream(filename);
            byte[] temp = new byte[1024];
            int len = 0;
            while ((len = input.read(temp)) > 0) {
                sb.append(new String(temp, 0, len));
            }
            input.close();
        }
        return sb.toString();
    }
}
