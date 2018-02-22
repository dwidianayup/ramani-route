package com.ujuizi.ramani.routing.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ujuizi.ramani.routing.R;
import com.ujuizi.ramani.routing.helper.JourneyModel;
import com.ujuizi.ramani.routing.helper.SearchAddressModel;
import com.ujuizi.ramani.routing.listener.RoutingListener;
import com.ujuizi.ramani.routing.navigation.RouteNavigation;
import com.ujuizi.ramani.sdk.android.MapView;
import com.ujuizi.ramani.sdk.android.input.MapEventListener;
import com.ujuizi.ramani.sdk.android.location.GPSLocationManager;
import com.ujuizi.ramani.sdk.core.GeoPoint;

import java.util.ArrayList;

import static com.ujuizi.ramani.routing.view.SearchAddressView.STATUS_END_POINT;
import static com.ujuizi.ramani.routing.view.SearchAddressView.STATUS_START_POINT;

/**
 * Created by ujuizi on 1/6/17.
 */

public class PlanJourneyView {
    private MapEventListener mMapEventListener;
    private MapView mMapDummy;
    private DirectionsView directionsView;
    private Activity mActivity;
    private Context mContext;

    private MapView mMap;
    private View navigationLayout;

    private RouteNavigation mRouteNavigation;

    private GPSLocationManager gpsLocationManager;


    String[] items;
    private TextView btnStartPoint;
    private TextView btnEndPoint;
    private LinearLayout btnClear;
    private RadioGroup radioVehicleType;
    private LinearLayout navigationView;
    private RelativeLayout headerNavView;
    private RelativeLayout.LayoutParams paramBottom;
    private RelativeLayout.LayoutParams paramNormal;
    private String selectMapStatus = "";
    private BroadcastReceiver setReceiverSearch;

    private SearchAddressView searchAddressView;
    private ImageButton btnNavigation;
    private boolean isDefaultView = true;

    /**
     * Navigation view is only views to set Vehicle Type, Start/End Point, Clear Route, Directions by Default.
     * if you want to create Custom Navigation View just Create your view and set by RouteNavigation without PlanJourneyView
     *
     * @param context
     * @param map
     * @param routeNavigation
     */
    public PlanJourneyView(Context context, MapView map, RouteNavigation routeNavigation) {
        mContext = context;
        mMap = map;
        mMapDummy = map;
        mActivity = (Activity) context;
        mRouteNavigation = routeNavigation;
        this.gpsLocationManager = mMap.gpsLocationManager;
        items = new String[]{context.getString(R.string.your_location), context.getString(R.string.select_on_map), context.getString(R.string.search_address)};
        isDefaultView = true;
        initNavigationView();


    }

    public PlanJourneyView(Context context, MapView map, RouteNavigation routeNavigation, View navigationView) {
        mContext = context;
        mMap = map;
        mMapDummy = map;
        mActivity = (Activity) context;
        mRouteNavigation = routeNavigation;
        this.gpsLocationManager = mMap.gpsLocationManager;
        navigationLayout = navigationView;
        if (navigationLayout != null) {
            isDefaultView = false;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            mActivity.addContentView(navigationView, params);
        }
    }

    private RoutingListener.SearchAddressListener searchAddressListener = new RoutingListener.SearchAddressListener() {
        @Override
        public void onSearchAddressDone(ArrayList<SearchAddressModel> searchData) {

        }

        @Override
        public void onSelectedItemSearch(SearchAddressModel searchData, int status) {
            searchAddressView.hideSearchLayout();
            GeoPoint point = new GeoPoint(searchData.getLat(), searchData.getLon());
            if (status == STATUS_START_POINT) {
                mRouteNavigation.setStartPoint(point);
            } else if (status == STATUS_END_POINT) {
                mRouteNavigation.setEndPoint(point);
            }
            mMap.map().setMapPosition(point, 15);
        }
    };

    private void initNavigationView() {
        removeView();

        mMapDummy.setGestureListener(new MapEventListener() {
            @Override
            public boolean onDoubleTap(GeoPoint geoPoint) {

                return mMapEventListener == null || mMapEventListener.onDoubleTap(geoPoint);
            }

            @Override
            public boolean onSingleTapUp(GeoPoint geoPoint) {
                Log.e("MapPosition", "Bearing : " + mMap.map().getMapPosition().getBearing());
                Log.e("MapPosition", "Tilt : " + mMap.map().getMapPosition().getTilt());
                Log.e("MapPosition", "ZoomScale : " + mMap.map().getMapPosition().getZoomScale());
                Log.e("MapPosition", "X : " + mMap.map().getMapPosition().getX());
                Log.e("MapPosition", "Y : " + mMap.map().getMapPosition().getY());
                return mMapEventListener == null || mMapEventListener.onSingleTapUp(geoPoint);
            }

            @Override
            public boolean onLongPress(GeoPoint geoPoint) {

                Log.e("longpress", "geopoint : " + geoPoint.toString());
                if (selectMapStatus.equalsIgnoreCase("start")) {
                    Toast.makeText(mActivity, "Start : " + geoPoint.getLongitude() + "," + geoPoint.getLongitude(), Toast.LENGTH_SHORT).show();
                    mRouteNavigation.setStartPoint(geoPoint);
                    btnNavigation.performClick();
                    return true;

                } else if (selectMapStatus.equalsIgnoreCase("end")) {
                    mRouteNavigation.setEndPoint(geoPoint);
                    Toast.makeText(mActivity, "End : " + geoPoint.getLongitude() + "," + geoPoint.getLongitude(), Toast.LENGTH_SHORT).show();
                    btnNavigation.performClick();
                    return true;
                }

                return mMapEventListener == null || mMapEventListener.onLongPress(geoPoint);
            }
        });

        LinearLayout relativeLayout = new LinearLayout(mActivity);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        navigationLayout = inflater.inflate(R.layout.navigation_view, relativeLayout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        btnNavigation = (ImageButton) navigationLayout.findViewById(R.id.btnNavigation);
        headerNavView = (RelativeLayout) navigationLayout.findViewById(R.id.header_nav_view);

        navigationView = (LinearLayout) navigationLayout.findViewById(R.id.view_naviagtions);
        LinearLayout btnDirections = (LinearLayout) navigationLayout.findViewById(R.id.btn_directions);
        btnClear = (LinearLayout) navigationLayout.findViewById(R.id.btn_clear_directions);
        btnStartPoint = (TextView) navigationLayout.findViewById(R.id.btn_start);
        btnEndPoint = (TextView) navigationLayout.findViewById(R.id.btn_end);
        radioVehicleType = (RadioGroup) navigationLayout.findViewById(R.id.view_vehicle_type);
//        navigationLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (planJourneyView.getVisibility() == View.VISIBLE) {
//                    ViewAnimationUtils.collapse(planJourneyView);
//                    btnNavigation.setVisibility(View.VISIBLE);
//
//                }
//            }
//        });
        headerNavView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (navigationView.getVisibility() == View.VISIBLE) {
                    ViewAnimationUtils.collapse(navigationView);
                    btnNavigation.setVisibility(View.VISIBLE);

                } else {
                    btnNavigation.setVisibility(View.GONE);
                    ViewAnimationUtils.expand(navigationView);

                }

            }
        });

        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navigationView.getVisibility() == View.VISIBLE) {
                    ViewAnimationUtils.collapse(navigationView);
                    btnNavigation.setVisibility(View.VISIBLE);
                } else {
                    ViewAnimationUtils.expand(navigationView);
                    btnNavigation.setVisibility(View.GONE);

                }
            }
        });
        radioVehicleType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            Toast toast;

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (toast != null)
                    toast.cancel();
                // find which radio button is selected
                if (checkedId == R.id.radio_by_car) {
                    toast = Toast.makeText(mActivity, R.string.by_car,
                            Toast.LENGTH_SHORT);
                    mRouteNavigation.setVehicleType(RouteNavigation.BY_CAR);
                } else if (checkedId == R.id.radio_by_bike) {
                    Toast.makeText(mActivity, R.string.by_bike,
                            Toast.LENGTH_SHORT);
                    mRouteNavigation.setVehicleType(RouteNavigation.BY_BIKE);
                } else {
                    toast = Toast.makeText(mActivity, R.string.by_foot,
                            Toast.LENGTH_SHORT);
                    mRouteNavigation.setVehicleType(RouteNavigation.BY_FOOT);
                }

                if (toast != null)
                    toast.show();

            }
        });

        btnStartPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
                        android.R.layout.simple_spinner_dropdown_item, items);
                new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle(R.string.start_point)
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btnStartPoint.setText(items[which]);
                                switch (which) {
                                    case 0:
                                        if (gpsLocationManager != null) {
                                            Location loc = gpsLocationManager.getLastKnownLocation();
                                            if (loc != null) {
                                                mRouteNavigation.setStartPoint(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
                                                Toast.makeText(mActivity, items[0], Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                        Toast.makeText(mActivity, R.string.location_null, Toast.LENGTH_SHORT).show();

                                        break;
                                    case 1:
                                        Toast.makeText(mActivity, items[1], Toast.LENGTH_SHORT).show();
                                        btnNavigation.performClick();
                                        selectMapStatus = "start";
                                        break;
                                    case 2:
                                        Toast.makeText(mActivity, items[2], Toast.LENGTH_SHORT).show();
//                                        mActivity.startActivity(new Intent(mActivity, SearchViewActivity.class).putExtra("status", "start"));
                                        searchAddressView.showSearchLayout(STATUS_START_POINT);
                                        break;

                                }


                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });

        btnEndPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
                        android.R.layout.simple_spinner_dropdown_item, items);
                new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle(R.string.end_point)
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btnEndPoint.setText(items[which]);
                                switch (which) {
                                    case 0:
                                        Location loc = gpsLocationManager.getLastKnownLocation();
                                        if (gpsLocationManager != null && loc != null) {

                                            mRouteNavigation.setEndPoint(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
                                            Toast.makeText(mActivity, items[0], Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(mActivity, R.string.location_null, Toast.LENGTH_SHORT).show();
                                        }

                                        break;
                                    case 1:
                                        Toast.makeText(mActivity, items[1], Toast.LENGTH_SHORT).show();
                                        btnNavigation.performClick();
                                        selectMapStatus = "end";
                                        break;
                                    case 2:
                                        Toast.makeText(mActivity, items[2], Toast.LENGTH_SHORT).show();
//                                        mActivity.startActivity(new Intent(mActivity, SearchViewActivity.class).putExtra("status", "end"));
                                        searchAddressView.showSearchLayout(SearchAddressView.STATUS_END_POINT);
                                        break;

                                }

                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });


        btnDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mRouteNavigation.getStartPoint() == null) {
                    if (btnStartPoint.getText().equals(items[0])) {
                        if (gpsLocationManager != null) {
                            Location loc = gpsLocationManager.getLastKnownLocation();
                            if (loc != null) {
                                mRouteNavigation.setStartPoint(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
                                Toast.makeText(mActivity, items[0], Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mActivity, R.string.location_null, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    } else {
                        Toast.makeText(mActivity, R.string.start_point_still_empty, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (mRouteNavigation.getEndPoint() == null) {
                    Toast.makeText(mActivity, R.string.end_point_still_empty, Toast.LENGTH_SHORT).show();
                } else if (mRouteNavigation.getStartPoint() != null && mRouteNavigation.getEndPoint() != null) {
                    generateRoute();
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMapStatus = "";
                btnStartPoint.setText(items[0]);
                btnEndPoint.setText("Set End Point");
                mRouteNavigation.clearRoute();
            }
        });

        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mActivity.addContentView(navigationLayout, params);

        directionsView = new DirectionsView(mActivity, mMap, mRouteNavigation.getNavigator());
        searchAddressView = new SearchAddressView(mActivity, searchAddressListener);
    }

    private void generateRoute() {


        mRouteNavigation.generatingRoute(new RoutingListener.GenerateRouteListener() {
            ProgressDialog progressDialog;

            @Override
            public void onRoutingPrepare() {
                progressDialog = new ProgressDialog(mActivity);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage(mActivity.getString(R.string.please_wait));
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog.show();
            }

            @Override
            public void onRoatingProgress(String progressName, float progress) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.setMessage(progressName);
                    progressDialog.setProgress((int) progress);
                }
                Log.e("GenerateRouteListener", "progressName : " + progressName);
                Log.e("GenerateRouteListener", "progress : " + progressName);
            }

            @Override
            public void onRoatingDone(JourneyModel journeyObj) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (journeyObj.getGeoPoints().size() > 0) {
                    directionsView.setJourneyData(journeyObj);
                    directionsView.showDirectionView();
                    btnNavigation.performClick();
                }
            }

            @Override
            public void onRoatingFailed(String message) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(mActivity, "error generating route", Toast.LENGTH_SHORT).show();
                Log.e("GenerateRouteListener", "message : " + message);
            }


        });
    }

    private void removeView() {
        if (navigationLayout != null) {
            try {
                ViewGroup parent = (ViewGroup) navigationLayout.getParent();
                parent.removeView(navigationLayout);
                navigationLayout = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setMapEvenListener(MapEventListener mapEvenListener) {
        this.mMapEventListener = mapEvenListener;
    }

    public void setNavigationListener(RoutingListener.NavigationListener navigationListener) {
        directionsView.setNavigationListener(navigationListener);
    }

    public DirectionsView getDirectionsView() {
        if (directionsView != null) return directionsView;

        return null;
    }
}
