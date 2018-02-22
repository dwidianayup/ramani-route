package com.ujuizi.ramani.routing.helper;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ujuizi on 1/10/17.
 */
public class SearchAddressModel implements Serializable{

    private long placeID;
    private String license;
    private String osmType;
    private long osmID;
    private Double[] boundingBox;
    private Double lat;
    private Double lon;
    private String displayName;
    private String classType;

    private String typePlace;
    private String iconPlace;

    private JSONObject jsonResult;

    public void setJsonResult(JSONObject jsonResult) {
        this.jsonResult = jsonResult;
    }

    public JSONObject getJsonResult() {
        return jsonResult;
    }

    public void setPlaceID(long placeID) {
        this.placeID = placeID;
    }

    public long getPlaceID() {
        return placeID;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicense() {
        return license;
    }

    public void setOsmID(long osmID) {
        this.osmID = osmID;
    }

    public long getOsmID() {
        return osmID;
    }

    public void setOsmType(String osmType) {
        this.osmType = osmType;
    }

    public String getOsmType() {
        return osmType;
    }

    public void setBoundingBox(Double[] boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Double[] getBoundingBox() {
        return boundingBox;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLat() {
        return lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLon() {
        return lon;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getClassType() {//place
        return classType;
    }

    public void setIconPlace(String iconPlace) {
        this.iconPlace = iconPlace;
    }

    public String getIconPlace() {
        return iconPlace;
    }

    public void setTypePlace(String typePlace) {
        this.typePlace = typePlace;
    }

    public String getTypePlace() {//city or either
        return typePlace;
    }


}
