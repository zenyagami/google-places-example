package com.zenkun.estimote.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Zen zenyagami@gmail.com on 23/02/2017.
 */

public class ModelVenue {
    public String venueName;
    public  String photoReferenceUrl;
    public LatLng latLng;
    //type for recyclerView
    public int type=1;

    public ModelVenue() {
    }

    public ModelVenue(int type) {
        this.type = type;
    }
}
