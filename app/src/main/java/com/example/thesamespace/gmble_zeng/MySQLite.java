package com.example.thesamespace.gmble_zeng;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by thesamespace on 2016/1/18.
 */
public class MySQLite extends SQLiteOpenHelper {
    public MySQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MYBLE(id INTEGER PRIMARY KEY AUTOINCREMENT,name CHAR(20),distance INTEGER,rssi INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("ALTER TABLE person ADD phone VARCHAR(12) NULL");
    }

    public void createTable(SQLiteDatabase db, String tebleName) {
        db.execSQL("CREATE TABLE " + tebleName + "(id INTEGER PRIMARY KEY AUTOINCREMENT,distance INTEGER,rssi INTEGER)");
    }
}
