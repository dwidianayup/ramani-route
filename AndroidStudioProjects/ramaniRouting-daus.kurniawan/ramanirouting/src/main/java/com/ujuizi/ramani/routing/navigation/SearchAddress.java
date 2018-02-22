package com.ujuizi.ramani.routing.navigation;

import android.util.Log;

import com.ujuizi.ramani.routing.helper.SearchAddressModel;
import com.ujuizi.ramani.routing.utils.GetDataFromURL;
import com.ujuizi.ramani.sdk.core.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ujuizi on 12/30/16.
 */

public class SearchAddress {


    private static String MAIN_URL = "https://nominatim.openstreetmap.org/search.php?format=json";
    private String json;

    private GetDataFromURL getDataFromURL = new GetDataFromURL();

    public ArrayList<SearchAddressModel> searchAddressFromPoint(GeoPoint point) {
        if (point != null) {
            JSONArray json = getJSONFromUrl(MAIN_URL + "&q=" + point.getLatitude() + "," + point.getLongitude());
            return (getAddressDataFromJson(json));
        } else {
            Log.e("SearchAddress", "point == null");
        }
        return new ArrayList<SearchAddressModel>();
    }

    public ArrayList<SearchAddressModel> searchAddressByName(String addressName) {
        if (addressName != null && !addressName.equalsIgnoreCase("")) {
            JSONArray json = getJSONFromUrl(MAIN_URL + "&q=" + addressName);
            return (getAddressDataFromJson(json));
        } else {
            Log.e("SearchAddress", "addresname empty");
        }
        return new ArrayList<SearchAddressModel>();
    }


    private JSONArray getJSONFromUrl(String URL) {

        json = getDataFromURL.getDataWithoutAsyntask(URL);
        JSONArray jObj = null;
        if (json != null && !json.equalsIgnoreCase("error") && !json.equalsIgnoreCase("time out")) {

            try {
                if (json.equals("[]")) {
                    jObj = null;
                } else {
                    jObj = new JSONArray(json);
                }

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
        }

        // return JSON String
        return jObj;
    }

    private ArrayList<SearchAddressModel> getAddressDataFromJson(JSONArray jsonArray) {
        ArrayList<SearchAddressModel> lisData = new ArrayList<>();

        if (jsonArray != null) {
            try {
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject c = jsonArray.getJSONObject(i);
                        SearchAddressModel data = new SearchAddressModel();

                        data.setJsonResult(c);
                        data.setPlaceID(c.getLong("place_id"));
                        data.setLicense(c.getString("licence"));
                        data.setOsmType(c.getString("osm_type"));
                        data.setOsmID(c.getLong("osm_id"));
                        data.setLat(c.getDouble("lat"));
                        data.setLon(c.getDouble("lon"));
                        data.setDisplayName(c.getString("display_name"));
                        data.setClassType(c.getString("class"));
                        data.setTypePlace(c.getString("type"));
                        data.setIconPlace(c.isNull("icon") ? "" : c.getString("icon"));
                        Double[] boundingbox = new Double[4];
                        for (int in = 0; in < c.getJSONArray("boundingbox").length(); in++) {
                            boundingbox[in] = c.getJSONArray("boundingbox").getDouble(in);
                            Log.e("boundingbox", " " + boundingbox[in]);
                        }
                        data.setBoundingBox(boundingbox);
                        lisData.add(data);

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return lisData;
    }

}
