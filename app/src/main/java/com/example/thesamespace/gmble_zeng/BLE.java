package com.example.thesamespace.gmble_zeng;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

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
    private boolean enabled = true;
    private double variance = 0;
    private double distance = 0;

    public BLE(BluetoothDevice device, int rssi) {
        this.mDevice = device;
        this.lastRssi = rssi;
        this.rssiList.add(rssi);
        rssiSum = rssi;
    }

    public void upDate(int rssi) {
        this.lastRssi = rssi;
        this.rssiList.add(rssi);
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
        Integer i = new Integer(mble.rssiSum);
        return i.compareTo(this.rssiSum);
    }

    public int compareTo(Object another, int a) {
        BLE mble = (BLE) another;
        if (rssiList.size() < a) {
            Integer i = new Integer(mble.lastRssi);
            return i.compareTo(this.lastRssi);
        } else {
            Integer i = new Integer(mble.rssiSum);
            return i.compareTo(this.rssiSum);
        }
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
        double avg = rssiSum / this.rssiList.size();
        double sum = 0;
        for (int i = 0; i < rssiList.size(); i++) {
            sum += Math.pow(rssiList.get(i) - avg, 2);
        }
        return sum / rssiList.size();
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
