package com.ujuizi.ramani.routing.listener;

import android.location.Location;

import com.ujuizi.ramani.routing.helper.JourneyModel;
import com.ujuizi.ramani.routing.helper.SearchAddressModel;
import com.ujuizi.ramani.sdk.core.GeoPoint;

import java.util.ArrayList;

/**
 * Created by ujuizi on 12/29/16.
 */

public interface RoutingListener {

    interface OfflineRoutingListener {
        void onRoatingDone(boolean isReady);
    }

    interface GenerateRouteListener {
        void onRoutingPrepare();

        void onRoatingProgress(String progressName, float progress);

        void onRoatingDone(JourneyModel journeyObj);

        void onRoatingFailed(String message);
    }

    interface SearchAddressListener {
        void onSearchAddressDone(ArrayList<SearchAddressModel> searchData);

        void onSelectedItemSearch(SearchAddressModel searchData, int status);
    }

    interface NavigationListener {
        void onNavigationStart();

        void onNavigationUpdate(Location location, GeoPoint point);

        void onNavigationStop();

    }

    interface SimulationListener {
        void onSimulationStart();

        void onSimulationUpdate();

        void onSimulationStop();

    }
}
