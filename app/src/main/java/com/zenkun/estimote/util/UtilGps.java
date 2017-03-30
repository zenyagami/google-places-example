package com.zenkun.estimote.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Zen zenyagami@gmail.com on 23/02/2017.
 */

public class UtilGps {
    public static LatLng getBestLastKnowPosition(Context context)
    {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location;

        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //if location == null we try to use another providers, we can iterare based on current providers too and select the one with more accuracy..
        if(location==null)
            location=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location==null)
            location=lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        if(location!=null)
            return new LatLng(location.getLatitude(),location.getLongitude());

        return null;


    }

}
