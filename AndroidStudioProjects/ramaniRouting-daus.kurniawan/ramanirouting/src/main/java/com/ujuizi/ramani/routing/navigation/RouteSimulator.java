package com.ujuizi.ramani.routing.navigation;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.ujuizi.ramani.routing.R;
import com.ujuizi.ramani.routing.helper.JourneyModel;
import com.ujuizi.ramani.routing.listener.RoutingListener;
import com.ujuizi.ramani.routing.utils.Gaussian;
import com.ujuizi.ramani.routing.utils.LatLngInterpolator;
import com.ujuizi.ramani.routing.view.NavigatorView;
import com.ujuizi.ramani.sdk.android.MapView;
import com.ujuizi.ramani.sdk.android.input.Marker;
import com.ujuizi.ramani.sdk.core.GeoPoint;
import com.ujuizi.ramani.sdk.core.MapPosition;
import com.ujuizi.ramani.sdk.layers.marker.MarkerItem;

/**
 * Created by ujuizi on 8/7/17.
 */

public class RouteSimulator {

    private NavigatorView navigatorView;
    private MapView mapview;
    private Activity activity;
    private Marker trackMarker;
    private boolean simulationStarted = true;
    private JourneyModel journeyData;
    private Runnable runnableAnimation;
    private int currentIndex = 0;
    private GeoPoint pointA;
    private GeoPoint pointB;

    private LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Linear();
    private double oldBearing;
    private ValueAnimator mAnimator;
    private Handler handler = new Handler();
    private int zoomlevel;
    private RoutingListener.SimulationListener simulationListener;
    private View viewSimulation;

    private boolean isPaused = false;
    private FloatingActionButton btn_media;

    public RouteSimulator(Activity activity, MapView mapView, NavigatorView navigatorView) {
        this.activity = activity;
        this.mapview = mapView;
        this.navigatorView = navigatorView;

        trackMarker = new Marker(mapView, activity);
        trackMarker.setIcon(activity.getResources().getDrawable(R.drawable.driving_blue));
        trackMarker.setMarkerSpot(MarkerItem.HotspotPlace.CENTER);
        trackMarker.setTitle("Marker Simulator");
        trackMarker.setDescription("This is Marker Simulator");
        trackMarker.setIndexLayer(6);
        trackMarker.setToCenter(false);

        navigatorView.addNavigatorChangeListener(new NavigatorView.AddNavigatorChangeListener() {
            public int positionTmp = 0;

            @Override
            public void onAddNavigatorChangeListener(int position) {

                if (isPaused) {
                    handler.removeCallbacks(runnableAnimation);
                    runnableAnimation = null;
                    int i = 0;
                    Log.e("NavigatorChangeListener", "getGeoPoints size : " + journeyData.getGeoPoints().size());
                    for (GeoPoint point :
                            journeyData.getGeoPoints()) {
                        if (journeyData.getInstructions().get(position).getPoints().getLatitude(0) == point.getLatitude() &&
                                journeyData.getInstructions().get(position).getPoints().getLongitude(0) == point.getLongitude()) {
                            Log.e("NavigatorChangeListener", "journeyData.getInstructions() size : " + journeyData.getInstructions().size());
                            Log.e("NavigatorChangeListener", "same data : " + i);
                            Log.e("NavigatorChangeListener", "name : " + journeyData.getInstructions().get(position).getName());
                            break;
                        }
                        i += 1;
                    }

                    currentIndex = i;
                    runSimulation(currentIndex);
                    if (currentIndex > 0 && currentIndex < journeyData.getGeoPoints().size()) {
                        pointA = journeyData.getGeoPoints().get(currentIndex - 1);
                        pointB = journeyData.getGeoPoints().get(currentIndex);
                    } else {
                        pointA = journeyData.getGeoPoints().get(currentIndex);
                        pointB = journeyData.getGeoPoints().get(currentIndex + 1);
                    }
                    trackMarker.removeMarker();
                    trackMarker.setToCenter(false);
                    trackMarker.setIndexLayer(6);
                    trackMarker.add(pointA);

                    MapPosition mapPosition = RouteSimulator.this.mapview.map().getMapPosition();
                    mapPosition.setPosition(pointA);
                    mapPosition.setBearing(360 - (float) pointA.bearingTo(pointB));
                    long duration = 10 + ((long) pointA.distanceTo(pointB)/10);
                    RouteSimulator.this.mapview.map().animator().animateTo(duration, mapPosition);
//                RouteSimulator.this.mapview.map().setMapPosition(mapPosition);
//                RouteSimulator.this.mapview.map().viewport().setRotation(360 - (float) pointA.bearingTo(pointB));
                    setPauseSimulator(true);
                }
            }
        });
    }

    public void setListener(RoutingListener.SimulationListener simulationListener) {
        this.simulationListener = simulationListener;
    }

    public void setTrackMarker(Marker trackMarker) {
        this.trackMarker = trackMarker;
    }

    public void setJourneyData(JourneyModel journeyData) {
        this.journeyData = journeyData;
    }

    private int i = 5;

    public void startSimulation() {
        simulationStarted = true;
        if (simulationListener != null) simulationListener.onSimulationStart();
        navigatorView.setJourNeyData(journeyData);
        showSimulationButton();
        runSimulation(0);

    }

    private void runSimulation(int index) {

        Log.e("startSimulation", "index : " + index);
        mGaussian = new Gaussian((maxAlpha - minAlpha) / 4, (minAlpha + maxAlpha) / 2);
        currentIndex = index;
        zoomlevel = mapview.map().getMapPosition().zoomLevel;
        if (journeyData != null && simulationStarted) {
            if (currentIndex < journeyData.getGeoPoints().size()) {
                i = 5;
                if (runnableAnimation == null) {
                    runnableAnimation = new Runnable() {
                        @Override
                        public void run() {
                            if (activity == null || activity.isFinishing()) return;
                            if (!simulationStarted) return;
                            if (simulationListener != null) simulationListener.onSimulationUpdate();

                            Interpolator interpolator = new LinearInterpolator();
                            try {
                                pointA = journeyData.getGeoPoints().get(currentIndex);
                                pointB = journeyData.getGeoPoints().get(currentIndex + 1);

                                Log.e("startSimulation", "index : " + currentIndex);
                                Log.e("startSimulation", "run : " + pointA.toString());
                                Log.e("startSimulation", "run : " + pointB.toString());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            float duration = (float) pointA.distanceTo(pointB) * 10;
                            float elapsed = (30 - zoomlevel) * i;
                            float t = interpolator.getInterpolation(elapsed / duration);
//                            float t = interpolator.getInterpolation((float) elapsed / duration);

                            Log.e("startSimulation", "t : " + t);
                            if (t < 1.0 && !isPaused) {
                                // Post again 20ms later.
                                GeoPoint newPosition = latLngInterpolator.interpolate(t, pointA, pointB);
                                trackMarker.removeMarker();
                                trackMarker.setToCenter(false);
                                trackMarker.setIndexLayer(6);
                                trackMarker.add(newPosition);
                                navigatorView.navigation(newPosition, 1);

                                float newBearing = (float) newPosition.bearingTo(pointB);

                                Log.e("startSimulation", "newBearing : " + newBearing);
                                Log.e("startSimulation", "oldBearing : " + oldBearing);
                                Log.e("startSimulation", "Math.abs(newBearing - oldBearing) : " + Math.abs(newBearing - oldBearing));

                                MapPosition mapPosition = mapview.map().getMapPosition();
                                mapPosition.setPosition(newPosition);
                                mapview.map().viewport().setMapPosition(mapPosition);
                                mapview.map().viewport().setMapScreenCenter(0.5f);
                                mapview.map().viewport().setRotation(360 - newBearing);
                                if (oldBearing == 0) {
                                    oldBearing = newBearing;
                                }
                                if (Math.abs(newBearing - oldBearing) > 5) {
                                    rotateMap(newBearing, (float) oldBearing);
                                }

                                oldBearing = newBearing;
                                handler.postDelayed(this, 210);

                                i = i + 5;
                            } else if (isPaused) {
                                handler.removeCallbacks(runnableAnimation);
                                runnableAnimation = null;
                            } else {
                                runSimulation(currentIndex + 1);
                            }

                            mapview.map().updateMap(true);
                        }
                    };
                    handler.postDelayed(runnableAnimation, 430);
                } else {
                    handler.postDelayed(runnableAnimation, 430);

                }
                return;
            }
        }

        stopSimulation();
        if (simulationListener != null) simulationListener.onSimulationStop();

    }

    private void removeView() {
        trackMarker.removeMarker();
        navigatorView.hiddenNavigatorView();
        if (viewSimulation != null) {
            viewSimulation.setVisibility(View.GONE);
        }
    }

    public void stopSimulation() {
        simulationStarted = false;
        removeView();
        if (journeyData != null) {
            if (journeyData.getGeoPoints().size() > 0) {
                MapPosition mapPosition = mapview.map().getMapPosition();
                mapPosition.setPosition(journeyData.getGeoPoints().get(0));
                mapview.map().viewport().setMapPosition(mapPosition);
            }
        }

    }

    public boolean isSimulationStarted() {
        return simulationStarted;
    }


    double maxAlpha = 0.5;
    double minAlpha = 0.1;
    Gaussian mGaussian;
    double y = 0;

    private void rotateMap(float bearingNew, float bearingOld) {
        if (mAnimator != null) {
            if (mAnimator.isRunning()) {
                mAnimator.end();
            }
        }
        mAnimator = slideAnimator(bearingOld, bearingNew, true);
        mAnimator.setDuration(3000);
        if (!mapview.isHardwareAccelerated()) {
            mapview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        mAnimator.start();
    }


    private ValueAnimator slideAnimator(final float start, final float end, final boolean gaussianRotate) {

        ValueAnimator animator = ValueAnimator.ofFloat(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            float bearing = start;
            float oBearing = start;
            int i = 1;
            int filetertime = (int) Math.abs(start - end);

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                if (gaussianRotate) {
                    if (filetertime < 20) {
                        filetertime = 20;
                    }
                    double ax = minAlpha + ((float) i / filetertime) * (maxAlpha - minAlpha);
                    y = mGaussian.getY(ax);
                    bearing = Gaussian.exponentialSmoothing(oBearing, end, (float) y);
                    oBearing = bearing;
                } else {
                    bearing = (float) valueAnimator.getAnimatedValue();
                }

                mapview.map().viewport().setRotation((double) -bearing);
                mapview.map().updateMap(true);


                i++;

            }
        });
        return animator;
    }

    private void initSimulationButton() {
        if (viewSimulation == null) {
            LinearLayout relativeLayout = new LinearLayout(activity);

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewSimulation = inflater.inflate(R.layout.simulator_button_layout, relativeLayout);


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            activity.addContentView(viewSimulation, params);

            btn_media = (FloatingActionButton) viewSimulation.findViewById(R.id.btn_media_simulation);
            btn_media.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isPaused) {
                        setPauseSimulator(true);
                    } else {
                        setPauseSimulator(false);
                        runSimulation(currentIndex);
                    }
                }
            });
        }
    }

    public void showSimulationButton() {
        initSimulationButton();
        viewSimulation.setVisibility(View.VISIBLE);
    }


    public void setPauseSimulator(boolean value) {
        isPaused = value;
        if (!value) {
            btn_media.setImageResource(android.R.drawable.ic_media_pause);

        } else {
            btn_media.setImageResource(android.R.drawable.ic_media_play);
            runSimulation(currentIndex);
        }

    }


}
