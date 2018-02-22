package com.ujuizi.ramani.routing.graphopper;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.Path;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.TranslationMap;
import com.ujuizi.ramani.routing.helper.MapMathingHelper;
import com.ujuizi.ramani.routing.listener.RoutingListener;
import com.ujuizi.ramani.sdk.core.GeoPoint;

import java.io.File;

/**
 * Created by ujuizi on 8/10/17.
 */

public class OfflineGraphopper {

    private String TAG = "OfflineGraphopper";
    private File pathFile;
    private String currentArea;
    public GraphHopper hopper;
    //    private LocationIndex locationIndex;
    private boolean isReady = false;
    private RoutingListener.OfflineRoutingListener mListener;


    MapMathingHelper mapMatchingHelper = new MapMathingHelper();

    public OfflineGraphopper(String areaName, String FolderPath,
                             RoutingListener.OfflineRoutingListener loadRoatingListener) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pathFile = new File(Environment.getExternalStoragePublicDirectory(""), FolderPath);
        } else {
            pathFile = new File(FolderPath);
        }

        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        Log.d(TAG, "PATH : " + pathFile.toString());

        currentArea = areaName;
        mListener = loadRoatingListener;
        loadGraphStorage();
    }

    public void loadGraphStorage() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                hopper = new GraphHopper().forMobile();
                //hopper.setCHEnable(false);
                //hopper.setCHWeightings("fastest");
                hopper.getCHFactoryDecorator().setWeightingsAsStrings("fastest");


//                int preciseIndexResolution = mPreferences.getInt("preciseIndexResolution", 300);
//                if (preciseIndexResolution > 0) {
//                    hopper.setPreciseIndexResolution(preciseIndexResolution);
//                }


//                int minNetworkSize = mPreferences.getInt("minNetworkSize", 200);
//                Log.e("GH", "minNetworkSize:" + minNetworkSize + " \t preciseIndexResolution:" + preciseIndexResolution);
//                int minOneWayNetworkSize = mPreferences.getInt("minOneWayNetworkSize", 0);
//                if (minNetworkSize > 0) {
//                    hopper.setMinNetworkSize(minNetworkSize, minOneWayNetworkSize);
//                }
                boolean result = false;
//
                try {
                    result = hopper.load(new File(pathFile.toString(), currentArea).getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;

            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
//                    locationIndex = hopper.getLocationIndex();
                    isReady = true;
                }

                if (mListener != null)
                    mListener.onRoatingDone(isReady);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public boolean isRoutingReady() {
        return isReady;
    }

    public Path getRoutingPath(GeoPoint startPoint, GeoPoint endPoint, String vehicleType) {

        if (isReady) {
            if (startPoint != null && endPoint != null)
                return mapMatchingHelper.MapMaptchingData(startPoint.getLatitude(), startPoint.getLongitude(), endPoint.getLatitude(), endPoint.getLongitude(), vehicleType, hopper);
        } else
            Log.e("getRoutingPath", "Routing Data not Ready yet");
        return null;

    }

    public InstructionList getInstructionList(Path path) {
        if (path != null && path.isFound())
            return path.calcInstructions(new TranslationMap().doImport().get(""));

        return null;
    }
}
