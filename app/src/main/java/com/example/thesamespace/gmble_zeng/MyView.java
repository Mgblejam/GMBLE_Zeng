package com.example.thesamespace.gmble_zeng;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thesamespace on 2016/1/17.
 */
public class MyView extends View  {
    private Paint paintBeacon;
    private Paint paintMe;
    private List<Beacon> beacons = new ArrayList<Beacon>();
    private PointF myPoint = new PointF();
    private Bitmap bitmap = null;
    private Rect rect;
    private Rect rect2;

    public Rect getRect2() {
        return rect2;
    }

    public void setRect2(Rect rect2) {
        this.rect2 = rect2;
    }

    /*内部类Beacon，用来存储Beacon的文字描述和坐标*/
    class Beacon {
        String describe;
        PointF pointF = new PointF();

        public Beacon(String name, float x, float y) {
            this.describe = name;
            this.pointF.set(x, y);
        }
    }

    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /*init(),用来初始化paint画笔*/
    private void init() {
        paintBeacon = new Paint();
        paintBeacon.setAntiAlias(true);
        paintBeacon.setStyle(Paint.Style.FILL);
        paintBeacon.setTextSize(24);
        paintBeacon.setStrokeWidth(10);

        paintMe = new Paint(paintBeacon);
        paintMe.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, rect, rect2, paintBeacon);
        }
        for (Beacon beacon : beacons) {
            canvas.drawCircle(beacon.pointF.x, beacon.pointF.y, 9, paintBeacon);
            canvas.drawText(beacon.describe, beacon.pointF.x + 12, beacon.pointF.y + 8, paintBeacon);
        }
        if (myPoint.x > 0) {
            canvas.drawCircle(myPoint.x, myPoint.y, 9, paintMe);
            canvas.drawText("Me", myPoint.x + 12, myPoint.y + 8, paintMe);
        }
    }

    /*addBeaconPoint()，用来把新的Beacon坐标添加到beacons中*/
    public void addBeaconPoint(String name, float x, float y) {
        beacons.add(new Beacon(name, x, y));
        invalidate();
    }

    /*更新我的坐标*/
    public void updateMyPoint(float x, float y) {
        this.myPoint.set(x, y);
        invalidate();
    }

    public void setBitmap(int imgID) {
        bitmap = BitmapFactory.decodeResource(getResources(), imgID);
        rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }
}
