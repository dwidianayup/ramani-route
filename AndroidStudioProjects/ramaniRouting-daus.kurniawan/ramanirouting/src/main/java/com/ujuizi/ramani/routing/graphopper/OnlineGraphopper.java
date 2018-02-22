package com.ujuizi.ramani.routing.graphopper;

import android.util.Log;

import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionAnnotation;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;
import com.graphhopper.util.TranslationMap;
import com.ujuizi.ramani.routing.helper.InstructionModel;
import com.ujuizi.ramani.routing.helper.JourneyModel;
import com.ujuizi.ramani.routing.helper.SendRequest;
import com.ujuizi.ramani.routing.utils.Utils;
import com.ujuizi.ramani.sdk.core.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ujuizi on 8/11/17.
 */

public class OnlineGraphopper {

    private String vehicleType = "car";
    private SendRequest sendRequest = new SendRequest();
    private static final String URL_REQUEST = "http://a.ramani.ujuizi.com/routing/route?";

    //    private static final String URL_REQUEST = "http://a.ramani.ujuizi.com/routing/route?point=45.752193%2C-0.686646&point=46.229253%2C-0.32959"
    public OnlineGraphopper() {
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public JourneyModel getRoute(GeoPoint startPoint, GeoPoint endPoint) throws Exception {
        String points = "point=" + startPoint.getLatitude() + "," + startPoint.getLongitude()
                + "&point=" + endPoint.getLatitude() + "," + endPoint.getLongitude()
                + "&vehicle=" + vehicleType
                + "&points_encoded=false";
        String result = sendRequest.getResponseString(URL_REQUEST + points);

        if (result != null && !result.equalsIgnoreCase("error") && !result.equalsIgnoreCase("time out")) {
            try {
                String error_message = getErrorMessage(result);
                if (!error_message.equalsIgnoreCase("no error message")) {
                    throw new Exception(error_message, new Throwable(error_message));
                }
                JSONObject jsonObject = new JSONObject(result);

                JSONObject pathJshon = jsonObject.getJSONArray("paths").getJSONObject(0);

                JourneyModel journeyModel = new JourneyModel();
                ArrayList<InstructionModel> instructionList = getInstructionArrayList(pathJshon);
                journeyModel.setInstructionObj(instructionList);
                journeyModel.setDistance(pathJshon.getDouble("distance"));
                journeyModel.setTotalNode(instructionList.size());
                journeyModel.setVehicleType(vehicleType);
                journeyModel.setDistanceTime(pathJshon.getLong("time"));
                journeyModel.setGeoPoints(getGeopoints(pathJshon));
                journeyModel.setInstructions(getInstructionList(pathJshon));
                return journeyModel;
            } catch (JSONException e) {
                e.printStackTrace();
                throw new Exception(e.getMessage(), new Throwable(e.toString()));

            }

        }
        throw new Exception(result, new Throwable(result));
    }

    public ArrayList<InstructionModel> getInstructionArrayList(JSONObject pathJson) {
        try {
            JSONArray instructions = pathJson.getJSONArray("instructions");

            ArrayList<InstructionModel> instructionList = new ArrayList<>();

            if (instructions.length() > 0) {
                for (int i = 0; i < instructions.length(); i++) {
                    InstructionModel data = new InstructionModel();
                    data.setTitle(instructions.getJSONObject(i).isNull("street_name") ?
                            "No Road Name" : instructions.getJSONObject(i).getString("street_name"));
                    data.setDistance(instructions.getJSONObject(i).getDouble("distance"));
                    data.setDistanceName(Utils.distanceConverter(data.getDistance()));
                    data.setIcon(Utils.getIconManuver(instructions.getJSONObject(i).getInt("sign")));
                    data.setManuver(Utils.getManuverName(instructions.getJSONObject(i).getInt("sign")));
                    data.setTime(instructions.getJSONObject(i).getLong("time"));


//                    data.setPointList(instructions.getJSONObject(i).getLong("time"));

                    Log.e("getInstructionList", "title : " + data.getTitle());
                    Log.e("getInstructionList", "Distance : " + data.getDistance());
                    Log.e("getInstructionList", "DistanceName : " + data.getDistanceName());
                    Log.e("getInstructionList", "Icon : " + data.getIcon());
                    Log.e("getInstructionList", "Manuver : " + data.getManuver());
                    Log.e("getInstructionList", "time : " + data.getTime());

                    instructionList.add(data);
                }
            }
            return instructionList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public InstructionList getInstructionList(JSONObject pathJson) {
        try {
            JSONArray jsonArray = pathJson.getJSONArray("instructions");

            InstructionList instructionList = new InstructionList(new TranslationMap().doImport().get(""));

            ArrayList<GeoPoint> geoPoints = getGeopoints(pathJson);

            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {

                    int intervalStart = jsonArray.getJSONObject(i).getJSONArray("interval").getInt(0);
                    int intervalEnd = jsonArray.getJSONObject(i).getJSONArray("interval").getInt(1);
                    PointList pointList = new PointList();

                    Log.e("pointList", "intervalStart : " + intervalStart);
                    Log.e("pointList", "intervalEnd : " + intervalEnd);
                    for (int j = intervalStart; j < geoPoints.size(); j++) {
                        pointList.add(geoPoints.get(j).getLatitude(),
                                geoPoints.get(j).getLongitude());

                        Log.e("pointList", "point " + j + " : " + geoPoints.get(j).getLatitude() + "," + geoPoints.get(j).getLongitude());
                        if (j == intervalEnd) {
                            break;
                        }
                    }


                    InstructionAnnotation instructionAnnotation = new InstructionAnnotation(0, "");
                    Instruction data = new Instruction(jsonArray.getJSONObject(i).getInt("sign"),
                            jsonArray.getJSONObject(i).isNull("street_name") ?
                                    "No Road Name" : jsonArray.getJSONObject(i).getString("street_name"),
                            instructionAnnotation,
                            pointList);

                    data.setDistance(jsonArray.getJSONObject(i).getDouble("distance"));
                    data.setTime(jsonArray.getJSONObject(i).getLong("time"));

//                    data.setTitle(instructions.getJSONObject(i).isNull("street_name") ? "No Road Name" : instructions.getJSONObject(0).getString("street_name"));
//                    data.setDistance(instructions.getJSONObject(i).getDouble("distance"));
//                    data.setDistanceName(Utils.distanceConverter(data.getDistance()));
//                    data.setIcon(Utils.getIconManuver(instructions.getJSONObject(i).getInt("sign")));
//                    data.setManuver(Utils.getManuverName(instructions.getJSONObject(i).getInt("sign")));
//                    data.setTime(instructions.getJSONObject(i).getLong("time"));


//                    data.setPointList(instructions.getJSONObject(i).getLong("time"));

                    Log.e("getInstructionList", "title : " + data.getName());
                    Log.e("getInstructionList", "Distance : " + data.getDistance());
                    Log.e("getInstructionList", "DistanceName : " + Utils.distanceConverter(data.getDistance()));
                    Log.e("getInstructionList", "Icon : " + Utils.getIconManuver(data.getSign()));
                    Log.e("getInstructionList", "Manuver : " + Utils.getManuverName(data.getSign()));
                    Log.e("getInstructionList", "time : " + data.getTime());

                    instructionList.add(data);
                }
            }
            return instructionList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<GeoPoint> getGeopoints(JSONObject pathJson) {
        try {
            JSONArray jsonCoordinates = pathJson.getJSONObject("points").getJSONArray("coordinates");

            if (jsonCoordinates.length() > 0) {
                ArrayList<GeoPoint> points = new ArrayList<>();
                for (int i = 0; i < jsonCoordinates.length(); i++) {
                    Double lon = jsonCoordinates.getJSONArray(i).getDouble(0);
                    Double lat = jsonCoordinates.getJSONArray(i).getDouble(1);
                    Log.e("getGeopoints", "lat : " + lat);
                    Log.e("getGeopoints", "lon : " + lon);
                    GeoPoint geoPoint = new GeoPoint(lat, lon);
                    points.add(geoPoint);
                }
                return points;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getErrorMessage(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            if (!jsonObject.isNull("message")) {
                return jsonObject.getString("message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "no error message";
    }


}
