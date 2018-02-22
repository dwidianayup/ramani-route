package com.ujuizi.ramani.routing.view;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ujuizi.ramani.routing.R;
import com.ujuizi.ramani.routing.helper.JourneyModel;
import com.ujuizi.ramani.routing.listener.RoutingListener;
import com.ujuizi.ramani.routing.navigation.RouteSimulator;
import com.ujuizi.ramani.routing.utils.Utils;
import com.ujuizi.ramani.routing.view.adapter.DirectionsAdapter;
import com.ujuizi.ramani.sdk.android.MapView;
import com.ujuizi.ramani.sdk.core.MapPosition;

import static com.ujuizi.ramani.routing.utils.Utils.getBearing;

/**
 * Created by ujuizi on 1/9/17.
 */

public class DirectionsView {

    public RouteSimulator simulator;
    private Activity mActivity;
    private MapView mMapView;
    private View directionsLayout;
    private ListView listViewDirect;
    private DirectionsAdapter adapter;
    private LinearLayout btnClose;
    private TextView textDistanceTime;
    private TextView textDetailsDirection;
    //    private TextView textTotalNode;
    private LinearLayout btnStartNavigations;

    public NavigatorView navigator;
    private JourneyModel journeyData;
    private FloatingActionButton btnStartSimulations;

    public DirectionsView(Activity activity, MapView mapView, NavigatorView navigator) {
        mActivity = activity;
        mMapView = mapView;
        this.navigator = navigator;

        simulator = new RouteSimulator(activity, mapView, navigator);
        initDirectionView();
    }

    public void setNavigationListener(RoutingListener.NavigationListener navigationListener) {
        navigator.setNavigationListener(navigationListener);
    }

    private void initDirectionView() {
        removeDirectionView();
        LinearLayout relativeLayout = new LinearLayout(mActivity);

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        directionsLayout = inflater.inflate(R.layout.directions_view, relativeLayout);
        directionsLayout.setVisibility(View.GONE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        listViewDirect = (ListView) directionsLayout.findViewById(R.id.listview_directions);
        btnClose = (LinearLayout) directionsLayout.findViewById(R.id.close_directions);
        btnStartNavigations = (LinearLayout) directionsLayout.findViewById(R.id.start_navigations);
        btnStartSimulations = (FloatingActionButton) directionsLayout.findViewById(R.id.start_simulations);
        textDistanceTime = (TextView) directionsLayout.findViewById(R.id.text_distance_time);
        textDistanceTime.setSelected(true);
        textDetailsDirection = (TextView) directionsLayout.findViewById(R.id.text_details_value);
//        textTotalNode = (TextView) directionsLayout.findViewById(R.id.text_total_node);
        textDistanceTime = (TextView) directionsLayout.findViewById(R.id.text_distance_time);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDirections();
            }
        });

        btnStartNavigations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navigator.startNavigator();
                mMapView.setMyLocationMarker(mActivity.getResources().getDrawable(R.drawable.driving_blue));
                startNavigations();
            }
        });
        btnStartSimulations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNavigations();

                simulator.setJourneyData(journeyData);
                simulator.startSimulation();
            }
        });

        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mActivity.addContentView(directionsLayout, params);
    }

    private void startNavigations() {

        hideDirections();
        navigator.showNavigatorView(0,journeyData.getInstructionObj().get(0));
//                mMapView.setCameraDistance(18f);
        MapPosition mapPosition = new MapPosition();
        mapPosition.setZoomLevel(19);
        mapPosition.setPosition(journeyData.getGeoPoints().get(0).getLatitude(), journeyData.getGeoPoints().get(0).getLongitude());
        float bearing = getBearing(journeyData.getGeoPoints().get(0), journeyData.getGeoPoints().get(1));

        mapPosition.setBearing(bearing);
        mapPosition.setTilt(75);
        mMapView.map().setMapPosition(mapPosition);

    }


    public void removeDirectionView() {
        if (directionsLayout != null) {
            try {
                ViewGroup parent = (ViewGroup) directionsLayout.getParent();
                parent.removeView(directionsLayout);
                directionsLayout = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showDirectionView() {
        if (directionsLayout.getVisibility() == View.GONE) {
            textDistanceTime.setText(Utils.durationConverter(journeyData.getDistanceTime()) + " (" + Utils.distanceConverter(journeyData.getDistance()) + ")");
            textDetailsDirection.setText("Fastest route by " + journeyData.getVehicleType());
//        textTotalNode.setText("Total Road : " + String.valueOf(journeyObj.getTotalNode()));

            adapter = new DirectionsAdapter(mActivity, journeyData);
            listViewDirect.setAdapter(adapter);
            listViewDirect.invalidateViews();
//            ViewAnimationUtils.expand(directionsLayout);
            directionsLayout.setVisibility(View.VISIBLE);
        }
    }

    public void hideDirections() {
        if (directionsLayout.getVisibility() == View.VISIBLE)
            directionsLayout.setVisibility(View.GONE);
//            ViewAnimationUtils.collapse(directionsLayout);
    }

    public void setJourneyData(JourneyModel journeyData) {
        this.journeyData = journeyData;
    }

    public JourneyModel getJourneyData() {
        return journeyData;
    }
}
