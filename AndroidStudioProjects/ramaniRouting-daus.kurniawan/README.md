Supported Platforms
=====================

With our Ramani API Framework, you can build apps that target native 32-bit or 64-bit devices running Android 4.0.3 and later.

Developing an application with the Ramani API Framework requires the following:

* Android SDK Tools 15 or later.


Latest Version
=====================

Latest Version from Ramani Routing is 0.0.2 on ramani artifact, and this version included Ramani Android (0.0.4).

Getting the Ramani Routing using Ramani MAPS-API for Android
=====================
To include our Ramani Routing in your Android project, add our libs into your dependencies section in your Project build.gradle.


```
allprojects {
    repositories {
        jcenter()
        maven {
            url 'http://team.ujuizi.com/archiva/repository/internal'
        }
    }
}
```

and also add our libs into your dependencies section in your app build.gradle.

```

dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:25.2.0'
    //add this dependency
    compile 'com.ujuizi.ramani:routing:0.0.2'
}

```
Then sync the Gradle, it will automatically download our dependency into your project (internet connection needed).


**Setup Permissions on Android Manifest.xml**

You need to allowed permissions in Manifest.xml to use ramaniRouting :

```

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.WRITE_INTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />


```


**Setup Online Routing**

You need to prepare .map file for your project and refresh the map. Then in your code :

```

public void initOnlineRouting() {

        //initialize your routeNavigation after refreshing a map
        routeNavigation = new RouteNavigation(mActivity, mMapView,true);
        routeNavigation.planJourneyView.setNavigationListener(this);
        routeNavigation.setMapEventListener(this);// use this method to call event listener

}

```


**Setup Offline Routing**

You need to prepare .map file for your project and refresh the map. Then in your code :

```

public void initOfflineRouting() {

        //initialize your routeNavigation after refreshing a map
        OfflineGraphopper offlineGraphopper = new OfflineGraphopper(AREA_NAME, FOLDER_PATH,
                new RoutingListener.OfflineRoutingListener() {
                    @Override
                    public void onRoatingDone(boolean isReady) {
                        Log.e("onRoatingDone", "isReady : " + isReady);
                        if (isReady){
                            // TO DO
                        }

                    }
                });

        routeNavigation = new RouteNavigation(mActivity, mMapView,true,offlineGraphopper);
        routeNavigation.planJourneyView.setNavigationListener(this);
        routeNavigation.setMapEventListener(this);// use this method to call event listener

}

```


**Generate Route**

To get Route, You need to set Start Point and also set End Point :

```

public void generateRoute() {

        GeoPoint startPoint = new GeoPoint(-6.990505,110.40088);
        GeoPoint endPoint = new GeoPoint(-7.791233,110.372484);

        routeNavigation.setStartPoint(startPoint);
        routeNavigation.setEndPoint(endPoint);
        routeNavigation.generatingRoute(this);//get JourneyModel from onRoatingDone

}

```


**Set Poly Line Size and Colors**

set PolyLine and Colors from Points which set from Start Point to EndPoint on Navigations :

```

        //set Line Navigation
        PolylineNavigation polylineNavigation = new PolylineNavigation(mMap, Color.GREEN,10f);
        routeNavigation.setPolylineNavigation(polylineNavigation);


```


**Get Address by Name and by Point**

To get Address, You need to set Point with GeoPoint Objects:

```

        // Call this inside Thread or Asyntask (non UI Thread)
public void searchAddress() {

        SearchAddress searchAddress = new SearchAddress();

        //Search Address base on Name or Place or City
        ArrayList<SearchAddressModel> listResultFromName = searchAddress.searchAddressByName("Semarang");
        if (listResultFromName.size() > 0){
            for (SearchAddressModel data: listResultFromName) {
                Log.e("searchAddress", "Display Name : " + data.getDisplayName());
            }
        }

        //Search Address base on Point (Latitude, Longitude)
        GeoPoint point = new GeoPoint(-6.990505,110.40088);
        ArrayList<SearchAddressModel> listResultFromPoint = searchAddress.searchAddressFromPoint(point);
        if (listResultFromPoint.size() > 0){
            for (SearchAddressModel data: listResultFromPoint) {
                Log.e("searchAddress", "Display Name : " + data.getDisplayName());
            }
        }

}

```

