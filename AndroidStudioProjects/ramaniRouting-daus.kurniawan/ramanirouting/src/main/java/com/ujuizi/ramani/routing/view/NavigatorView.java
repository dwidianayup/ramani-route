package com.ujuizi.ramani.routing.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.graphhopper.util.Instruction;
import com.ujuizi.ramani.routing.R;
import com.ujuizi.ramani.routing.helper.InstructionModel;
import com.ujuizi.ramani.routing.helper.JourneyModel;
import com.ujuizi.ramani.routing.listener.RoutingListener;
import com.ujuizi.ramani.routing.utils.Utils;
import com.ujuizi.ramani.sdk.android.MapView;
import com.ujuizi.ramani.sdk.core.GeoPoint;

import java.util.Locale;

/**
 * Created by ujuizi on 8/4/17.
 */

public class NavigatorView {


    private TextToSpeech textToSpeech;
    private Activity mActivity;
    private MapView mMapview;
    private RoutingListener.NavigationListener navigationListener;
    private View navigatorLayout;
    private ImageView iconDirection;
    private TextView textFlags;
    private TextView textRoadname;
    private TextView textDistance;

    private boolean isNavigatorStarted = false;
    private boolean enabledSpeech = true;

    private GeoPoint currentPoint;
    private GeoPoint currentPointTmp;
    private ViewPager mPagerNavigator;
    private JourneyModel journeyData;
    private InstructionModel instructionObj;
    private AddNavigatorChangeListener addNavigatorChangeListener;
    private ScreenSlidePagerAdapter mPagerAdapter;

    public NavigatorView(Activity activity, MapView mapView) {
        mMapview = mapView;
        mActivity = activity;

        textToSpeech = new TextToSpeech(mActivity, new TextToSpeech.OnInitListener() {
            @SuppressLint("NewApi")
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
        textToSpeech.setSpeechRate(0.8f);
        initViewPagerNavigation();
//        initNavigator();
    }

    public void setTextToSpeechStatus(boolean enabled) {
        enabledSpeech = enabled;
    }

    public boolean getTextToSpeechStatus() {
        return enabledSpeech;
    }

    private void initViewPagerNavigation() {
        // Instantiate a ViewPager and a PagerAdapter.
        LinearLayout relativeLayout = new LinearLayout(mActivity);

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        navigatorLayout = inflater.inflate(R.layout.navigator_view_pager, relativeLayout);

        mPagerNavigator = (ViewPager) navigatorLayout.findViewById(R.id.pager_navigator);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mActivity.addContentView(navigatorLayout, params);

        mPagerNavigator.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("addOnPageChangeListener", "onPageScrolled position : " + position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("addOnPageChangeListener", "onPageSelected position : " + position);
                if (addNavigatorChangeListener != null)
                    addNavigatorChangeListener.onAddNavigatorChangeListener(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("addOnPageChangeListener", "onPageScrollStateChanged state: " + state);
            }
        });

    }

    public void addNavigatorChangeListener(AddNavigatorChangeListener onNavigatorListener) {
        this.addNavigatorChangeListener = onNavigatorListener;
    }

//    private void initNavigator() {
//        LinearLayout relativeLayout = new LinearLayout(mActivity);
//
//        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        navigatorLayout = inflater.inflate(R.layout.navigator_view, relativeLayout);
//
//        hiddenNavigatorView();
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        iconDirection = (ImageView) navigatorLayout.findViewById(R.id.navigator_icon_direction);
//        textFlags = (TextView) navigatorLayout.findViewById(R.id.navigator_flags_text);
//        textDistance = (TextView) navigatorLayout.findViewById(R.id.navigator_distance_text);
//        textRoadname = (TextView) navigatorLayout.findViewById(R.id.navigator_roadname_text);
//
//        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        mActivity.addContentView(navigatorLayout, params);
//
//    }

    public void showNavigatorView(int item, InstructionModel instructionObj) {
        if (navigatorLayout.getVisibility() == View.GONE) {
            navigatorLayout.setVisibility(View.VISIBLE);
        }
        mPagerNavigator.setSelected(true);
        mPagerNavigator.setCurrentItem(item);

//        if (instructionObj != null) {
//            if (instructionObj.getTitle().equals("")) {
//                textRoadname.setText(" No Road Name");
//            } else {
//                textRoadname.setText(instructionObj.getTitle());
//            }
//            textDistance.setText(instructionObj.getDistanceName());
//            textFlags.setText(instructionObj.getManuver());
//            try {
//                iconDirection.setImageResource(instructionObj.getIcon());
//            } catch (Exception e) {
//                Log.e("NotificationAdapter", "Failure to get drawable id.", e);
//            }
//        }
    }

    public void hiddenNavigatorView() {
        if (navigatorLayout.getVisibility() == View.VISIBLE) {
            navigatorLayout.setVisibility(View.GONE);
        }
    }


    public void startNavigator() {

        setNavigatorStarted(true);
        if (navigationListener != null)
            navigationListener.onNavigationStart();
    }

    public void onNavigation(GeoPoint point) {
        if (navigationListener != null)
            navigationListener.onNavigationUpdate(null, point);
    }

    public void onNavigationStop() {
        setNavigatorStarted(false);
        if (navigationListener != null)
            navigationListener.onNavigationStop();
    }


    public void setJourNeyData(JourneyModel jourNeyData) {
        this.journeyData = jourNeyData;

        mPagerAdapter = new ScreenSlidePagerAdapter();
        mPagerAdapter.notifyDataSetChanged();
        mPagerNavigator.setAdapter(mPagerAdapter);
    }

    public void navigation(GeoPoint point, double maxDistance) {
        double lat = point.getLatitude();
        double lon = point.getLongitude();
        Instruction inst = journeyData.getInstructions().find(lat, lon, maxDistance);

        if (inst != null) {
            double distance = point.distanceTo(new GeoPoint(inst.getPoints().getLat(0), inst.getPoints().getLon(0)));
            instructionObj = new InstructionModel();

            String manuverName = Utils.getManuverName(inst.getSign());
            instructionObj.setTitle(inst.getName());
            instructionObj.setDistance(distance);
            instructionObj.setDistanceName(Utils.distanceConverter(instructionObj.getDistance()));
            instructionObj.setIcon(Utils.getIconManuver(inst.getSign()));
            instructionObj.setManuver(manuverName);
            instructionObj.setTime(inst.getTime());
            instructionObj.setPointList(inst.getPoints());
            String snippet = "";
            int i = 0;
            for (Instruction instruct :
                    journeyData.getInstructions()) {
                if (inst.toString().equals(instruct.toString())) {
                    break;
                }
                i += 1;
            }

            if (journeyData != null) {
                journeyData.getInstructionObj().set(i, instructionObj);
                mPagerAdapter.notifyDataSetChanged();
                mPagerNavigator.setAdapter(mPagerAdapter);
                mPagerNavigator.invalidate();
            }
            showNavigatorView(i, instructionObj);
            if ((distance >= 500) && (distance < 505)) {
                snippet = "in " + 500 + " meters, " + manuverName;
                setSpeech(snippet);
            } else if ((distance >= 200) && (distance < 205)) {
                snippet = "in " + 200 + " meters, " + manuverName;
                setSpeech(snippet);
            } else if ((distance >= 50) && (distance < 55)) {
                snippet = manuverName;
                setSpeech(snippet);
            }
            onNavigation(point);
        }
    }


    private void setSpeech(String speechText) {
        Log.e("setSpeech", "speechText : " + speechText);
        if (getTextToSpeechStatus()) {
            if (!textToSpeech.isSpeaking())
                textToSpeech.speak(speechText,
                        TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void setTextToSpeech(TextToSpeech textToSpeech) {
        this.textToSpeech = textToSpeech;
    }

    public void setNavigationListener(RoutingListener.NavigationListener navigationListener) {
        this.navigationListener = navigationListener;
    }

    public boolean isNavigatorStarted() {
        return isNavigatorStarted;
    }

    public void setNavigatorStarted(boolean navigatorStarted) {
        isNavigatorStarted = navigatorStarted;
    }

    public void setBearingNavigation(Location currentLocation) {
        if (currentLocation != null) {

            if (currentLocation.hasBearing()) {
                mMapview.map().viewport().setRotation(360 - currentLocation.getBearing());
            } else {
                currentPoint = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());

                if (currentPointTmp != null) {
                    float bearing = (float) currentPointTmp.bearingTo(currentPoint);
//                MapPosition mapPosition = mMapview.map().getMapPosition();
//                mapPosition.setBearing(bearing);
//                mMapview.map().setMapPosition(mapPosition);
                    mMapview.map().viewport().setRotation(360 - bearing);
                }

            }

            mMapview.map().viewport().setMapScreenCenter(0.5f);

            currentPointTmp = currentPoint;
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends PagerAdapter {

        private LayoutInflater mLayoutInflater;

        public ScreenSlidePagerAdapter() {
            mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (journeyData != null && journeyData.getInstructions().size() > 0) {
                return journeyData.getInstructions().size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == ((RelativeLayout) object));
        }

        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.navigator_view, container, false);

            iconDirection = (ImageView) itemView.findViewById(R.id.navigator_icon_direction);
            textFlags = (TextView) itemView.findViewById(R.id.navigator_flags_text);
            textDistance = (TextView) itemView.findViewById(R.id.navigator_distance_text);
            textRoadname = (TextView) itemView.findViewById(R.id.navigator_roadname_text);

            showView(journeyData.getInstructionObj().get(position));
            container.addView(itemView);

            return itemView;
        }

        private void showView(InstructionModel instructionObj) {
            if (instructionObj != null) {
                if (instructionObj.getTitle().equals("")) {
                    textRoadname.setText(" No Road Name");
                } else {
                    textRoadname.setText(instructionObj.getTitle());
                }
                textDistance.setText(instructionObj.getDistanceName());
                textFlags.setText(instructionObj.getManuver());
                try {
                    iconDirection.setImageResource(instructionObj.getIcon());
                } catch (Exception e) {
                    Log.e("NotificationAdapter", "Failure to get drawable id.", e);
                }
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof RelativeLayout)
                container.removeView((RelativeLayout) object);
        }
    }

    public interface AddNavigatorChangeListener {
        void onAddNavigatorChangeListener(int position);
    }
}
