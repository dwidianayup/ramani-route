package com.ujuizi.ramani.routing.helper;

import android.util.Log;

import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.matching.EdgeMatch;
import com.graphhopper.matching.MapMatching;
import com.graphhopper.matching.MatchResult;
import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.weighting.FastestWeighting;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.util.GPXEntry;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PathMerger;
import com.graphhopper.util.PointList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;
import com.graphhopper.util.shapes.GHPoint;
import com.ujuizi.ramani.sdk.core.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ujuizi on 1/13/17.
 */

public class MapMathingHelper {


    public Path mapMatching(List<GPXEntry> inputGPXEntries, String vehicleType, GraphHopper hopper) {


        String instructions = "";
        FlagEncoder firstEncoder = hopper.getEncodingManager().getEncoder(vehicleType);

        Log.e("mapmatching", "firstEncoder : " + firstEncoder.getMaxSpeed());
        Translation tr = new TranslationMap().doImport().get(instructions);


        Weighting weighting = new FastestWeighting(firstEncoder);
        Log.e("mapmatching", "weighting : " + weighting.getName());
        MapMatching mapMatching = new MapMatching(hopper, new AlgorithmOptions(Parameters.Algorithms.DIJKSTRA_BI, weighting));

        try {

            Log.e("mapmatching", "mapMatching : " + mapMatching.toString());
            MatchResult mr = mapMatching.doWork(inputGPXEntries);
            Log.e("mapmatching", "\tmatches:\t" + mr.getEdgeMatches().size() + ", gps entries:" + inputGPXEntries.size());
            Log.e("mapmatching", "\tgpx length:\t" + (float) mr.getGpxEntriesLength() + " vs " + (float) mr.getMatchLength());
            Log.e("mapmatching", "\tgpx time:\t" + mr.getGpxEntriesMillis() / 1000f + " vs " + mr.getMatchMillis() / 1000f);

            PathWrapper matchGHRsp = new PathWrapper();
            Path path = mapMatching.calcPath(mr);
            new PathMerger().doWork(matchGHRsp, Collections.singletonList(path), tr);
            InstructionList il = matchGHRsp.getInstructions();


            return path;
        } catch (Exception ex) {
            Log.e("MapMatching", "Error: " + ex.getMessage());
        }
        return null;
    }

    public Path mapMatching(PointList pointList, String vehicleType, GraphHopper hopper) {
        Log.e("mapMatching", "mapMatching");
        List<GPXEntry> inputGPXEntries = new ArrayList<>();
        if (pointList != null && pointList.getSize() > 0) {

            for (int i = 0; i < pointList.getSize(); i++) {
                GPXEntry gpxEntry = new GPXEntry(new GHPoint(pointList.getLat(i), pointList.getLon(i)), 0);
                inputGPXEntries.add(gpxEntry);
                Log.e("mapMatching", "add : " + i);
            }
//            Log.e("trackLog","inputGPXEntries "+inputGPXEntries.toString());
            return mapMatching(inputGPXEntries, vehicleType,hopper);
        }

        return null;

    }

    public Path mapMatching(ArrayList<GeoPoint> points, String vehicleType, GraphHopper hopper) {
        List<GPXEntry> inputGPXEntries = new ArrayList<>();
        if (points != null && points.size() > 0) {

            for (int i = 0; i < points.size(); i++) {
                GPXEntry gpxEntry = new GPXEntry(new GHPoint(points.get(i).getLatitude(), points.get(i).getLongitude()), 0);
                inputGPXEntries.add(gpxEntry);
            }
            Log.e("trackLog", "inputGPXEntries " + inputGPXEntries.toString());
            return mapMatching(inputGPXEntries, vehicleType,hopper);
        }

        return null;
    }


    public Path MapMaptchingData(double fromLat, double fromLon, double toLat, double toLon, String vehicleType, GraphHopper hopper) {
        try {

            // create MapMatching object, can and should be shared accross threads
            String algorithm = Parameters.Algorithms.DIJKSTRA_BI;
            FlagEncoder firstEncoder = hopper.getEncodingManager().getEncoder(vehicleType);
            Weighting weighting = new FastestWeighting(firstEncoder);
            AlgorithmOptions algoOptions = new AlgorithmOptions(algorithm, weighting);
            MapMatching mapMatching = new MapMatching(hopper, algoOptions);

            // do the actual matching, get the GPX entries from a file or via stream
            //        List<GPXEntry> inputGPXEntries = new GPXFile().doImport("nice.gpx").getEntries();
            List<GPXEntry> inputGPXEntries = new ArrayList<GPXEntry>();
            inputGPXEntries.add(new GPXEntry(fromLat, fromLon, 0));
            inputGPXEntries.add(new GPXEntry(toLat, toLon, 0));
            MatchResult mr = mapMatching.doWork(inputGPXEntries);

            // return GraphHopper edges with all associated GPX entries
            List<EdgeMatch> matches = mr.getEdgeMatches();
            // now do something with the edges like storing the edgeIds or doing fetchWayGeometry etc
            matches.get(0).getEdgeState();

            Path path = mapMatching.calcPath(mr);

//        Translation tr = new TranslationMap().doImport().get("");
//        return path.calcInstructions(tr);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MapMaptchingData", "error : " + e.getMessage());
        }
        return null;
    }


}
