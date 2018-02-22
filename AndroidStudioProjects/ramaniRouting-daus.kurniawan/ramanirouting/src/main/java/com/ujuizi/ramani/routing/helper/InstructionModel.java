package com.ujuizi.ramani.routing.helper;

import com.graphhopper.util.PointList;

/**
 * Created by ujuizi on 1/9/17.
 */

public class InstructionModel {

    private String title;
    private double distance;
    private String manuver;
    private String distanceName;
    private int icon;
    private long time;
    private PointList pointList;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistanceName(String distanceName) {
        this.distanceName = distanceName;
    }

    public String getDistanceName() {
        return distanceName;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {//result are R.drawable.name_drawable
        return icon;
    }

    public void setManuver(String manuver) {
        this.manuver = manuver;
    }

    public String getManuver() {
        return manuver;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setPointList(PointList pointList) {
        this.pointList = pointList;
    }

    public PointList getPointList() {
        return pointList;
    }
}
