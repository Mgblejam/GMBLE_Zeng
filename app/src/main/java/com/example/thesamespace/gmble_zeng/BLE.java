package com.example.thesamespace.gmble_zeng;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import org.altbeacon.beacon.logging.LogManager;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by thesamespace on 2016/1/3.
 */
public class BLE implements Comparable<Object> {
    public BluetoothDevice mDevice;
    private int lastRssi;
    private int color;
    public int rssiSum = 0;
    public ArrayList<Integer> rssiList = new ArrayList<Integer>();
    public ArrayList<Float> rssiAVGList = new ArrayList<Float>();
    private float rssiLastAVG = 0;
    private boolean enabled = true;
    private double variance = 0;
    private double distance = 0;
    private int txPower = -59;
    private float n = 0.3f;
    private String rssiStr="";

    public BLE(BluetoothDevice device, int rssi) {
        this.mDevice = device;
        this.lastRssi = rssi;
        this.rssiList.add(rssi);
        this.rssiLastAVG = rssi;
        rssiSum = rssi;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
        txPower = txPower;
    }

    public float getN() {
        return n;
    }

    public void setN(float n) {
        n = n;
    }

    public void upDate(int rssi) {
        this.lastRssi = rssi;
        this.rssiList.add(rssi);
        this.rssiLastAVG += (rssi - this.rssiLastAVG) * 0.1f;
        this.rssiAVGList.add(rssiLastAVG);
//        rssiStr+=rssi+",";
        rssiSum += rssi;
    }

    public int getLastRssi() {
        return lastRssi;
    }

    public void setLastRssi(int lastRssi) {
        this.lastRssi = lastRssi;
    }

    @Override
    public int compareTo(Object another) {
        BLE mble = (BLE) another;
//        Double i = new Double(mble.variance);
//        return i.compareTo(this.variance);

        Float i = new Float(mble.rssiLastAVG);
        return i.compareTo(this.rssiLastAVG);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getVariance() {
        if (rssiList.size() == 0) {
            return 0;
        }
        double avg = rssiSum / this.rssiList.size();
        double sum = 0;
        for (int i = 0; i < rssiList.size(); i++) {
            sum += Math.pow(rssiList.get(i) - avg, 2);
        }
        this.variance = sum / rssiList.size();
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public double getDistance() {
        return rssiToDistance(this.lastRssi, txPower, n);
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double calculateDistance(int txPower, double rssi) {
        double mCoefficient1 = 0;
        double mCoefficient2 = 0;
        double mCoefficient3 = 0;
        if (rssi == 0.0D) {
            return -1.0D;
        } else {
            double ratio = rssi * 1.0D / (double) txPower;
            double distance;
            if (ratio < 1.0D) {
                distance = Math.pow(ratio, 10.0D);
            } else {
                distance = mCoefficient1 * Math.pow(ratio, mCoefficient2) + mCoefficient3;
            }
            return distance;
        }
    }//来源谷歌beacon库

    public double rssiToDistance(int rssi, int txPower, float n) {
        return Math.pow(10, (rssi - txPower) / -10 * n);
    }//来源delphi APi

    public Float getRssiLastAVG() {
        return this.rssiLastAVG;
    }

    public String getRssiStr() {
        return rssiStr;
    }
}
