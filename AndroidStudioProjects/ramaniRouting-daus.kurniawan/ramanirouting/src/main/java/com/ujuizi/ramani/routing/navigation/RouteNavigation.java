package com.ujuizi.ramani.routing.navigation;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.graphhopper.routing.Path;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.ujuizi.ramani.routing.graphopper.OfflineGraphopper;
import com.ujuizi.ramani.routing.graphopper.OnlineGraphopper;
import com.ujuizi.ramani.routing.helper.InstructionModel;
import com.ujuizi.ramani.routing.helper.JourneyModel;
import com.ujuizi.ramani.routing.helper.SearchAddressModel;
import com.ujuizi.ramani.routing.listener.RoutingListener;
import com.ujuizi.ramani.routing.utils.Utils;
import com.ujuizi.ramani.routing.view.DirectionsView;
import com.ujuizi.ramani.routing.view.NavigatorView;
import com.ujuizi.ramani.routing.view.PlanJourneyView;
import com.ujuizi.ramani.sdk.android.MapView;
import com.ujuizi.ramani.sdk.android.input.MapEventListener;
import com.ujuizi.ramani.sdk.android.input.Marker;
import com.ujuizi.ramani.sdk.backend.canvas.Color;
import com.ujuizi.ramani.sdk.core.GeoPoint;
import com.ujuizi.ramani.sdk.core.MapPosition;

import java.util.ArrayList;

/**
 * Created by ujuizi on 12/29/16.
 */

public class RouteNavigation {


    private OfflineGraphopper offlineGraphopper;
    private NavigatorView navigator;
    private MapView mMap;
    private Context mContext;

    private boolean isOffline = false;

    private GeoPoint startPoint;
    private GeoPoint endPoint;

    private Marker startMarker;
    private Marker endMarker;

    private PolylineNavigation polylineNavigation;

//    private GHResponse mGHResponse;

    private String TAGGENERATE = "GenerateRoute";


    private String progressName = "Get Route";
    private SearchAddress searchAddress = new SearchAddress();

    private String startAddress = "";
    private String endAddress = "";
    private MapPosition mapPositionAfterGenerate;

    public PlanJourneyView planJourneyView;

    public static final String BY_CAR = "car";
    public static final String BY_BIKE = "bike";
    public static final String BY_FOOT = "foot";

    private String VEHICLE_TYPE = "car";

    private boolean isReRoute = true;

    private JourneyModel journeyData = new JourneyModel();
    private Path pathResponse;

    /**
     * Call this Constuctor to get Routing Data from ramani server (online mode)
     *
     * @param context context from your activity
     * @param map     add map for drawing markers and polylineNavigation
     */
    public RouteNavigation(Context context, MapView map, boolean defaultView) {
        mContext = context;
        mMap = map;

        if (defaultView) {
            navigator = new NavigatorView((Activity) context, mMap);//default layout navigator
            planJourneyView = new PlanJourneyView(mContext, map, this);//default layout navigation
        }
        startMarker = new Marker(mMap, mContext);
        endMarker = new Marker(mMap, mContext);

        startMarker.setTitle("Start Point");
        startMarker.setDescription("This is Start Point");

        endMarker.setTitle("End Point");
        endMarker.setDescription("This is End Point");

        polylineNavigation = new PolylineNavigation(mMap.map(), Color.parseColor("#499fd3"), 10f);

        isOffline = false;

    }

    /**
     * Call this Constuctor to use our View and get Routing Data from your directories (offline mode)
     *
     * @param context context from your activity
     * @param map     add map for drawing markers and polylineNavigation
     *                //     * @param areaName            set the area name from routing file (example : indonesia , ghana , netherlands , etc)
     *                //     * @param FolderPath          set the directories from your routing file (example : "/maindirectories/routing/")
     *                //     * @param loadRoatingListener set the listener to now your offline routing is ready or not
     */
    public RouteNavigation(Context context, MapView map, boolean defaultView, OfflineGraphopper offlineGraphopper) {
        this(context, map, defaultView);

        this.offlineGraphopper = offlineGraphopper;
        isOffline = true;

    }

    public void setMapEventListener(MapEventListener mapEventListener) {
        getPlanJourneyView().setMapEvenListener(mapEventListener);
    }

    /**
     * @param StartMarker set Your Custom Start Marker before add Start Point. Don't call this method to set default.
     */
    public void setStartMarker(Marker StartMarker) {
        this.startMarker = StartMarker;
    }

    /**
     * @return Get Start Marker
     */
    public Marker getStartMarker() {
        return startMarker;
    }


    /**
     * @param EndMarker set Your Custom End Marker before add End Point. Don't call this method to set default.
     */
    public void setEndMarker(Marker EndMarker) {
        this.endMarker = EndMarker;
    }

    /**
     * @return Get End Marker
     */
    public Marker getEndMarker() {
        return endMarker;
    }


    public void setStartPoint(GeoPoint startPoint) {

        clearStartPoint();
        mMap.setToFollowMyLocation(false);
        this.startPoint = startPoint;
        startMarker.add(startPoint);
        getAddress(this.startPoint, searchStartAddress);

        mapPositionAfterGenerate = new MapPosition(this.startPoint.getLatitude(), this.startPoint.getLongitude(), mMap.map().getMapPosition().getScale());

        isReRoute = true;
        mMap.map().updateMap(true);

    }

    public void setEndPoint(GeoPoint endPoint) {

        clearEndPoint();
        mMap.setToFollowMyLocation(false);
        this.endPoint = endPoint;
        endMarker.add(endPoint);
        getAddress(this.endPoint, searchEndAddress);
        isReRoute = true;
        mMap.map().updateMap(true);
    }


    public void clearStartPoint() {
        startAddress = "";
        startPoint = null;
        if (startMarker != null) {
            startMarker.removeMarker();
        }
        mMap.map().updateMap(true);
    }

    public void clearEndPoint() {
        endAddress = "";
        endPoint = null;

        if (endMarker != null) {
            endMarker.removeMarker();
        }
        mMap.map().updateMap(true);
    }

    public void setVehicleType(String vehicleType) {
        VEHICLE_TYPE = vehicleType;
        isReRoute = true;
    }

    public String getVehicleType() {
        return VEHICLE_TYPE;
    }

    public void generatingRoute(final RoutingListener.GenerateRouteListener routeListener) {

        if (isOffline) {
            getRouteOffline(routeListener);
        } else {
            getRouteOnline(routeListener);
        }
    }

    private OnlineGraphopper onlineGraphopper = new OnlineGraphopper();

    private void getRouteOnline(final RoutingListener.GenerateRouteListener routeListener) {
        if (startPoint != null && endPoint != null) {
            if (!isReRoute && journeyData != null) {
                if (routeListener != null) {
                    routeListener.onRoutingPrepare();
                    routeListener.onRoatingDone(journeyData);
                }
            } else {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();

                        if (routeListener != null)
                            routeListener.onRoutingPrepare();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            onlineGraphopper.setVehicleType(VEHICLE_TYPE);
                            journeyData = onlineGraphopper.getRoute(startPoint, endPoint);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Error", e.toString());
                            return e.toString();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String error) {
                        super.onPostExecute(error);
                        if (startPoint!=null) {
                            if (error == null && journeyData != null) {
                                polylineNavigation.createLine(journeyData.getGeoPoints());
                                mapPositionAfterGenerate = mMap.map().getMapPosition();
                                mapPositionAfterGenerate.setPosition(startPoint);
                                mMap.map().viewport().setMapPosition(mapPositionAfterGenerate);

                                isReRoute = false;

                                if (routeListener != null)
                                    routeListener.onRoatingDone(journeyData);
                                return;

                            } else {
                                if (routeListener != null)
                                    routeListener.onRoatingFailed(error);
                            }
                        }

                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }

    }

    public boolean isUpdateRoute(Location currentLocation) {

        Log.e("isUpdateRoute", "isUpdateRoute");
        if (currentLocation != null) {
            if (navigator != null && navigator.isNavigatorStarted()) {

                navigator.setBearingNavigation(currentLocation);

//                Log.e("isUpdateRoute", "navigator.isNavigatorStarted() : " + navigator.isNavigatorStarted());
                if (journeyData != null && journeyData.getInstructions().size() > 0) {

//                    Log.e("isUpdateRoute", "getInstructions().size() : " + journeyData.getInstructions().size());
                    Instruction inst = journeyData.getInstructions().find(currentLocation.getLatitude(), currentLocation.getLongitude(), 10);

                    GeoPoint current = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                    if (navigator != null)
                        navigator.navigation(current, 10);

                    if (inst != null) {
                        double distance = current.distanceTo(new GeoPoint(inst.getPoints().getLat(0), inst.getPoints().getLon(0)));
                        Log.e("isUpdateRoute", "distance :" + Utils.distanceConverter(distance));

                        return false;
                    } else {
                        if (!isReRoute) {
                            isReRoute = true;
                            startPoint = current;
                            generatingRoute(null);
                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void getRouteOffline(final RoutingListener.GenerateRouteListener routeListener) {
        polylineNavigation.clearLine();

        if (offlineGraphopper.isRoutingReady()) {

            if (startPoint != null && endPoint != null) {

                if (!isReRoute && journeyData != null) {
                    if (routeListener != null) {
                        routeListener.onRoutingPrepare();
                        routeListener.onRoatingDone(journeyData);
                    }
                } else {

                    new AsyncTask<Void, Integer, String>() {

                        private ArrayList<InstructionModel> instructionObj = new ArrayList<>();
                        private ArrayList<GeoPoint> geoPoints = new ArrayList<>();
                        private float progressVal = 0;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            if (routeListener != null)
                                routeListener.onRoutingPrepare();

                        }

                        @Override
                        protected String doInBackground(Void... params) {
                            String errorMessage = "";
                            try {

                                progressName = "Get Route";
                                progressVal = 10;
                                publishProgress();
                                if (startAddress.equalsIgnoreCase("")) {
                                    ArrayList<SearchAddressModel> data1 = searchAddress.searchAddressFromPoint(startPoint);
                                    if (data1 != null && data1.size() > 0 && !data1.get(0).getDisplayName().equalsIgnoreCase(""))
                                        startAddress = data1.get(0).getDisplayName();

                                }
                                if (endAddress.equalsIgnoreCase("")) {
                                    ArrayList<SearchAddressModel> data1 = searchAddress.searchAddressFromPoint(endPoint);
                                    if (data1 != null && data1.size() > 0 && !data1.get(0).getDisplayName().equalsIgnoreCase(""))
                                        endAddress = data1.get(0).getDisplayName();
                                }

                                progressName = "Load Route";
                                progressVal = 30;
                                publishProgress();

                                pathResponse = offlineGraphopper.getRoutingPath(startPoint, endPoint, VEHICLE_TYPE);

                                if (!pathResponse.isFound()) {

                                    errorMessage = "Route ot found";
                                    Log.e(TAGGENERATE, errorMessage);

                                } else {
                                    try {
                                        InstructionList mInstructionList = offlineGraphopper.getInstructionList(pathResponse);

                                        progressName = "Get Instructions";
                                        journeyData = new JourneyModel();
                                        journeyData.setInstructions(mInstructionList);
                                        progressVal = 50;
                                        publishProgress();
                                        instructionObj.clear();
                                        for (int i = 0; i < mInstructionList.size(); i++) {
                                            InstructionModel data = new InstructionModel();
                                            Log.d(TAGGENERATE, "title : " + mInstructionList.get(i).getName());
                                            Log.d(TAGGENERATE, "distance : " + mInstructionList.get(i).getDistance());
                                            Log.d(TAGGENERATE, "sign : " + mInstructionList.get(i).getSign());
                                            Log.d(TAGGENERATE, "DirectionName : " + Utils.getManuverName(mInstructionList.get(i).getSign()));
                                            Log.d(TAGGENERATE, "getMessage : " + mInstructionList.get(i).getAnnotation().getMessage());

                                            data.setTitle(mInstructionList.get(i).getName());
                                            data.setDistance(mInstructionList.get(i).getDistance());
                                            data.setDistanceName(Utils.distanceConverter(data.getDistance()));
                                            data.setIcon(Utils.getIconManuver(mInstructionList.get(i).getSign()));
                                            data.setManuver(Utils.getManuverName(mInstructionList.get(i).getSign()));
                                            data.setTime(mInstructionList.get(i).getTime());
                                            data.setPointList(mInstructionList.get(i).getPoints());


                                            instructionObj.add(data);
                                            progressVal = (float) (i / mInstructionList.size()) * 100;
                                            publishProgress();
                                        }

                                        progressName = "Get Points";

                                        /****************        MAP MATCHING FROM POINTLIST         *****************/

                                        Log.e(TAGGENERATE, "Total points : " + mInstructionList.size());
//                                        Path matched = mapMathingHelper.mapMatching(mGHResponse.getBest().getPoints(), VEHICLE_TYPE);
                                        publishProgress(80);

                                        geoPoints.clear();
                                        for (int i = 0; i < pathResponse.calcPoints().size(); i++) {
                                            Log.d(TAGGENERATE, "i : [" + i + "] listPoints : " +
                                                    "lat[" + pathResponse.calcPoints().getLat(i) + "]," +
                                                    "lon[" + pathResponse.calcPoints().getLon(i) + "]");
                                            geoPoints.add(new GeoPoint(pathResponse.calcPoints().getLat(i),
                                                    pathResponse.calcPoints().getLon(i)));
                                            progressVal = ((float) i / pathResponse.calcPoints().size()) * 100;
                                            publishProgress();
                                        }
//                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        errorMessage = e.getMessage();
                                    }
                                    Log.e(TAGGENERATE, "Generated finished");

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                errorMessage = e.getMessage();
                            }
                            progressVal = 100;
                            progressName = "Get Route Finished";
                            publishProgress();
                            return errorMessage;
                        }

                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            super.onProgressUpdate(values);
                            if (routeListener != null)
                                routeListener.onRoatingProgress(progressName, progressVal);
                        }

                        @Override
                        protected void onPostExecute(String message) {
                            super.onPostExecute(message);
                            try {
                                if (pathResponse != null && pathResponse.isFound()) {
                                    journeyData.setDistance(pathResponse.getDistance());
                                    journeyData.setDistanceTime(pathResponse.getTime());
                                    journeyData.setTotalNode(pathResponse.calcNodes().size());
                                    journeyData.setGeoPoints(geoPoints);
                                    journeyData.setInstructionObj(instructionObj);
                                    journeyData.setVehicleType(VEHICLE_TYPE);
                                    if (geoPoints != null && geoPoints.size() > 0) {
                                        polylineNavigation.createLine(geoPoints);
                                        mapPositionAfterGenerate = new MapPosition(startPoint.getLatitude(), startPoint.getLongitude(), mMap.map().getMapPosition().getScale());
                                        mMap.map().viewport().setMapPosition(mapPositionAfterGenerate);

                                        isReRoute = false;

                                        if (routeListener != null)
                                            routeListener.onRoatingDone(journeyData);
                                        return;
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                message = e.getMessage();
                            }

                            if (routeListener != null)
                                routeListener.onRoatingFailed(message);


                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        } else {
            Toast.makeText(mContext, "No Routing Data", Toast.LENGTH_SHORT).show();
            Log.e(TAGGENERATE, "Routing Data not Ready");
        }
    }

    public void setPolylineNavigation(PolylineNavigation polylineNavigation) {
        if (this.polylineNavigation != null) {
            this.polylineNavigation.clearLine();
        }
        this.polylineNavigation = polylineNavigation;
    }

    public PolylineNavigation getPolylineNavigation() {
        return polylineNavigation;
    }

    public void clearRoute() {
        isReRoute = true;
        clearStartPoint();
        clearEndPoint();
        getPolylineNavigation().clearLine();
        if (navigator != null) {
            navigator.onNavigationStop();
            navigator.hiddenNavigatorView();
        }
        mMap.map().updateMap(true);

        if (navigator != null && navigator.isNavigatorStarted())
            navigator.setNavigatorStarted(false);

        if (planJourneyView != null && planJourneyView.getDirectionsView() != null) {
            planJourneyView.getDirectionsView().removeDirectionView();
        }
    }


    /**
     * @param mapPositionAfterGenerate if you want to custom map position after generate rout,
     *                                 call this method after calling {@link RouteNavigation#setStartPoint(GeoPoint)} .
     */
    public void setMapPositionAfterGenerate(MapPosition mapPositionAfterGenerate) {
        this.mapPositionAfterGenerate = mapPositionAfterGenerate;
    }


    private void getAddress(final GeoPoint point, final RoutingListener.SearchAddressListener searchAddressListener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                searchAddressListener.onSearchAddressDone(searchAddress.searchAddressFromPoint(point));
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private RoutingListener.SearchAddressListener searchStartAddress = new RoutingListener.SearchAddressListener() {
        @Override
        public void onSearchAddressDone(ArrayList<SearchAddressModel> searchData) {
            if (searchData.size() > 0 && !searchData.get(0).getDisplayName().equalsIgnoreCase(""))
                startAddress = searchData.get(0).getDisplayName();
        }

        @Override
        public void onSelectedItemSearch(SearchAddressModel searchData, int status) {

        }

    };

    private RoutingListener.SearchAddressListener searchEndAddress = new RoutingListener.SearchAddressListener() {
        @Override
        public void onSearchAddressDone(ArrayList<SearchAddressModel> searchData) {
            if (searchData.size() > 0 && !searchData.get(0).getDisplayName().equalsIgnoreCase(""))
                endAddress = searchData.get(0).getDisplayName();
        }

        @Override
        public void onSelectedItemSearch(SearchAddressModel searchData, int status) {

        }

    };


    public GeoPoint getStartPoint() {
        return startPoint;
    }

    public GeoPoint getEndPoint() {
        return endPoint;
    }

    public NavigatorView getNavigator() {
        if (navigator != null) return navigator;

        return null;
    }

    public void setNavigatorView(NavigatorView navigatorView) {
        navigator = navigatorView;
    }

    public PlanJourneyView getPlanJourneyView() {
        if (planJourneyView != null) {
            return planJourneyView;
        }

        return null;
    }

    public void setPlanJourneyView(PlanJourneyView planJourneyView) {
        this.planJourneyView = planJourneyView;
    }

    public DirectionsView getDirectionsView() {
        if (planJourneyView != null) {
            if (planJourneyView.getDirectionsView() != null)
                return planJourneyView.getDirectionsView();
        }

        return null;
    }

    public void setDirectionsView(DirectionsView directionsView) {

    }

    public RouteSimulator getRouteSimulator() {
        if (getDirectionsView() != null) return getDirectionsView().simulator;

        return null;
    }
}
