package com.ujuizi.ramani.routingbuilder;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.ujuizi.ramani.routing.listener.RoutingListener;
import com.ujuizi.ramani.routing.navigation.RouteNavigation;
import com.ujuizi.ramani.sdk.android.MapView;
import com.ujuizi.ramani.sdk.android.account.RMAccountManager;
import com.ujuizi.ramani.sdk.android.cache.TileCache;
import com.ujuizi.ramani.sdk.android.input.MapEventListener;
import com.ujuizi.ramani.sdk.core.GeoPoint;
import com.ujuizi.ramani.sdk.layers.tile.bitmap.BitmapTileLayer;
import com.ujuizi.ramani.sdk.layers.tile.buildings.BuildingLayer;
import com.ujuizi.ramani.sdk.layers.tile.vector.VectorTileLayer;
import com.ujuizi.ramani.sdk.layers.tile.vector.labeling.LabelLayer;
import com.ujuizi.ramani.sdk.map.Layers;
import com.ujuizi.ramani.sdk.map.Map;
import com.ujuizi.ramani.sdk.theme.ThemeLoader;
import com.ujuizi.ramani.sdk.theme.VtmThemes;
import com.ujuizi.ramani.sdk.tiling.TileSource;
import com.ujuizi.ramani.sdk.tiling.source.bitmap.DefaultSources;
import com.ujuizi.ramani.sdk.tiling.source.oscimap4.OSciMap4TileSource;


public class MainActivity extends AppCompatActivity implements MapEventListener, RoutingListener.NavigationListener {
    private MapView mMapView;
    private Map mMap;
    private OSciMap4TileSource mTileSource;
    private TileCache mCache;
    private VectorTileLayer mBaseLayer;


    RouteNavigation navigation;

    private Activity mActivity;
    private FeedbackHelper feedbackHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;


        PermissionHelper permissionHelper = new PermissionHelper(this);
        permissionHelper.permissionListener(new PermissionHelper.PermissionListener() {
            @Override
            public void onPermissionCheckDone() {

            }
        });


        permissionHelper.checkAndRequestPermissions();

        RMAccountManager.init(mActivity, new RMAccountManager.RMAccountManagerListener() {
            @Override
            public void onMapAuthDone() {
                refreshMap();
            }
        }, "b6dc9b3033302cac36316cabc19c7c30");

        feedbackHelper = new FeedbackHelper(mActivity);

//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//
//                OnlineGraphopper onlineGraphopper = new OnlineGraphopper();
//                onlineGraphopper.getRoute(new GeoPoint(-7.005856, 110.470790), new GeoPoint(-6.993733, 110.420765));
//                return null;
//            }
//        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void refreshMap() {

        Log.e("MainActivity", "refreshMap");
        mMapView = (MapView) findViewById(R.id.mapView);

        mMap = mMapView.map();

        TileSource tileSource = DefaultSources.OPENSTREETMAP.build();


        mMap.setMapPosition(new GeoPoint(-6.983821, 110.410609), 16);
        TileCache mCache = new TileCache(this,null,"dataopenstreetmaps-tiles.db");
        mCache.setCacheSize(512 * (1 << 10));
        tileSource.setCache(mCache);

        VectorTileLayer mBaseLayer = mMap.setBaseMap(tileSource);
        mMap.layers().add(0,new BitmapTileLayer(mMap, tileSource));
        mMap.layers().add(1,new BuildingLayer(mMap, mBaseLayer));
        mMap.layers().add(2,new LabelLayer(mMap, mBaseLayer));
        mMap.setTheme(ThemeLoader.load(VtmThemes.OSMARENDER),true);
//        mMapView.setGestureListener(this);

        mMapView.setMyLocation(true, 0, 1);
        mMapView.setMyLocationButton(true);
        mMapView.setToFollowMyLocation(true);

        mMapView.setMyLocationListener(new MapView.MyLocationListener() {
            @Override
            public void onGPSStatusChanged(boolean b) {

            }

            @Override
            public void onLocationChanged(Location location) {
                Log.e("onLocationChanged", "onLocationChanged");
                if (navigation != null) {
                    boolean reRoute = navigation.isUpdateRoute(location);
                    Log.e("onLocationChanged", "isUpdateRoute : " + reRoute);

                }
            }
        });


        navigation = new RouteNavigation(mActivity, mMapView, true);
//        OfflineGraphopper offlineGraphopper = new OfflineGraphopper("indonesia_routing", "/indonesia_routing/",
//                new RoutingListener.OfflineRoutingListener() {
//                    @Override
//                    public void onRoatingDone(boolean isReady) {
//                        Log.e("onRoatingDone", "isReady : " + isReady);
//
//                    }
//                });

//        navigation = new RouteNavigation(this, mMapView,true,offlineGraphopper);
        navigation.planJourneyView.setNavigationListener(this);
        navigation.setMapEventListener(this);// use this method to call event listener


//        SearchAddress searchAddress = new SearchAddress();
//
//        //Search Address base on Name or Place or City
//        ArrayList<SearchAddressModel> listResultFromName = searchAddress.searchAddressByName("Semarang");
//        if (listResultFromName.size() > 0) {
//            for (SearchAddressModel data : listResultFromName) {
//                Log.e("searchAddress", "Display Name : " + data.getDisplayName());
//            }
//        }
//
//        //Search Address base on Point (Latitude, Longitude)
//        GeoPoint point = new GeoPoint(-6.990505, 110.40088);
//        ArrayList<SearchAddressModel> listResultFromPoint = searchAddress.searchAddressFromPoint(point);
//        if (listResultFromPoint.size() > 0) {
//            for (SearchAddressModel data : listResultFromPoint) {
//                Log.e("searchAddress", "Display Name : " + data.getDisplayName());
//            }
//        }

    }


    @Override
    public boolean onDoubleTap(GeoPoint geoPoint) {
        Log.e("setGestureListener", "onDoubleTap : " + geoPoint.toString());
        return true;
    }

    @Override
    public boolean onSingleTapUp(GeoPoint geoPoint) {
        Log.e("setGestureListener", "onSingleTapUp : " + geoPoint.toString());
        return true;
    }

    @Override
    public boolean onLongPress(GeoPoint geoPoint) {
        Log.e("setGestureListener", "onLongPress : " + geoPoint.toString());
        return true;
    }


    @Override
    public void onNavigationStart() {
        Log.e("NavigationListener", "onNavigationStart");
    }

    @Override
    public void onNavigationUpdate(Location location, GeoPoint point) {
        Log.e("NavigationListener", "onNavigationUpdate");
    }

    @Override
    public void onNavigationStop() {
        Log.e("NavigationListener", "onNavigationStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMapView != null)
            mMapView.gpsLocationManager.stopLocationService();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            feedbackHelper.feedback("ramanirouting");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        if (navigation.getNavigator() != null && navigation.getNavigator().isNavigatorStarted()) {
            navigation.clearRoute();
        } else if (navigation.getRouteSimulator() != null && !navigation.getRouteSimulator().isSimulationStarted()) {
            navigation.getRouteSimulator().stopSimulation();
        } else {
            super.onBackPressed();
        }
    }

}
