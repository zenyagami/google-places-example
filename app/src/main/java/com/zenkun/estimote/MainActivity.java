package com.zenkun.estimote;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zenkun.estimote.action.impl.ActionVenuesImpl;
import com.zenkun.estimote.adapter.AdapterVenues;
import com.zenkun.estimote.log.Logger;
import com.zenkun.estimote.model.ModelVenue;
import com.zenkun.estimote.util.UtilGps;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Subscription gpsSubscription;
    private ArrayList<ModelVenue> venueList;
    AdapterVenues adapterVenues;
    View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.myPictures);
        progressBar = findViewById(R.id.main_loading);
        venueList= new ArrayList<>();
        adapterVenues= new AdapterVenues(venueList);
        adapterVenues.setLoadMoreListener(() -> {
            mRecyclerView.post(() -> {
                int index = venueList.size() - 1;
                loadMore(index);
            });
            //Calling loadMore function in Runnable to fix the
            // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
        });
        adapterVenues.setOnItemClickListener(new AdapterVenues.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ModelVenue venue = venueList.get(position);

                startActivity(new Intent(getApplicationContext(),MapsActivity.class).putExtra("lat",venue.latLng.latitude)
                .putExtra("lng",venue.latLng.longitude).putExtra("name",venue.venueName));
            }
        });
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapterVenues);


        //check for GPS permissions, we need a screen to ask for permissions  but for now we ask at beginning
        new RxPermissions(this).request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        load(0);
                    } else {
                        Toast.makeText(getApplicationContext(),R.string.gps_permission_denied,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMore(int index) {
        if(index<100)
        {
            //separator
            venueList.add(new ModelVenue(0));
            adapterVenues.notifyItemInserted(venueList.size()-1);
            loadData(index);
        }
    }

    private void load(int index) {
        loadData(index);
    }

    private void loadData(int index) {
        //start asynctask or RX subscription...
        LatLng latLng = UtilGps.getBestLastKnowPosition(getApplicationContext());
        //if is null the Rx subsription will trhow an exception... is better for handling..
        gpsSubscription = new ActionVenuesImpl()
                .withParams(latLng,index)
                .observable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess,this::onError);



    }

    private void onSuccess(ArrayList<ModelVenue> modelVenues) {
        //remove last item

        if(progressBar.getVisibility()==View.VISIBLE)
            progressBar.setVisibility(View.GONE);

        if(adapterVenues.isLoading) {//remove loading view
            venueList.remove(venueList.size() - 1);
        }

        if(modelVenues!=null && modelVenues.size()>0)
        {
            //load data..
            venueList.addAll(modelVenues);
        }else if(adapterVenues.isLoading)
        {
            adapterVenues.setMoreDataAvailable(false);
            Logger.v("No more Data....");
        }

        adapterVenues.notifyDataChanged();;

        //mRecyclerView.setAdapter(mAdapter);
    }

    private void onError(Throwable throwable) {
        Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpsSubscription != null && !gpsSubscription.isUnsubscribed()) {
            gpsSubscription.unsubscribe();
        }
    }
}
