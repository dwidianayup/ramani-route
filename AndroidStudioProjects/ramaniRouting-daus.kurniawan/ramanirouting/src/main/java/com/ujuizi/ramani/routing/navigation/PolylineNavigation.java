package com.ujuizi.ramani.routing.navigation;

import com.ujuizi.ramani.sdk.backend.canvas.Color;
import com.ujuizi.ramani.sdk.core.GeoPoint;
import com.ujuizi.ramani.sdk.layers.PathLayer;
import com.ujuizi.ramani.sdk.map.Map;

import java.util.List;

/**
 * Created by ujuizi on 1/4/17.
 */

public class PolylineNavigation extends PathLayer {

    private Map mMap;

    public PolylineNavigation(Map map) {
      this(map,Color.parseColor("#499fd3"),7.0f);
    }

    public PolylineNavigation(Map map, int lineColor, float linesize) {
        super(map, lineColor, linesize);
        mMap = map;
    }

    public void createLine(List<GeoPoint> geoPoints) {

        clearLine();
        setPoints(geoPoints);

        mMap.layers().add(3, this);
    }


    public void clearLine() {
        clearPath();
        mMap.layers().remove(this);
    }
}
