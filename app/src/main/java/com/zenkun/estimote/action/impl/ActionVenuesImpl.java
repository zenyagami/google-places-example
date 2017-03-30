package com.zenkun.estimote.action.impl;

import com.google.android.gms.maps.model.LatLng;
import com.zenkun.estimote.action.ActionVenues;
import com.zenkun.estimote.log.Logger;
import com.zenkun.estimote.model.ModelVenue;
import com.zenkun.estimote.network.ApiCall;
import com.zenkun.estimote.network.NetClient;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Zen zenyagami@gmail.com on 23/02/2017.
 */

public class ActionVenuesImpl implements ActionVenues {
    private LatLng latLng;
    private int index;

    public ActionVenuesImpl withParams(LatLng latLng, int index) {
        this.latLng = latLng;
        this.index=index;
        return this;
    }

    @Override
    public Observable<ArrayList<ModelVenue>> observable() {
        return Observable.create(subscriber ->
        {
            try {
                if(latLng==null)
                {
                    Logger.v("LatLng null");
                    subscriber.onError(new Exception("GPS latLng is null"));
                }else
                {
                    subscriber.onNext(ApiCall.getVenueList(latLng,index));
                    subscriber.onCompleted();
                }

            }catch (Exception ex)
            {
                ex.printStackTrace();
                subscriber.onError(ex);
            }
        });
    }
}
