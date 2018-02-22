package com.ujuizi.ramani.routing.helper;

import com.graphhopper.util.InstructionList;
import com.ujuizi.ramani.sdk.core.GeoPoint;

import java.util.ArrayList;

/**
 * Created by ujuizi on 1/9/17.
 */

public class JourneyModel {

    private double distance;
    private long distanceTime;
    private int totalNode;

    private ArrayList<GeoPoint> geoPoints = new ArrayList<>();

    private ArrayList<InstructionModel> instructionObj = new ArrayList<>();
    private InstructionList instructions;
    private String vehicleType;



    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistanceTime(long distanceTime) {
        this.distanceTime = distanceTime;
    }

    public long getDistanceTime() {
        return distanceTime;
    }

    public void setTotalNode(int totalNode) {
        this.totalNode = totalNode;
    }

    public int getTotalNode() {
        return totalNode;
    }

    public void setGeoPoints(ArrayList<GeoPoint> geoPoints) {
        this.geoPoints = geoPoints;
    }

    public ArrayList<GeoPoint> getGeoPoints() {
        return geoPoints;
    }

    public void setInstructionObj(ArrayList<InstructionModel> instructionObj) {
        this.instructionObj = instructionObj;
    }

    public ArrayList<InstructionModel> getInstructionObj() {
        return instructionObj;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setInstructions(InstructionList instructions) {
        this.instructions = instructions;
    }

    public InstructionList getInstructions() {
        return instructions;
    }
}
