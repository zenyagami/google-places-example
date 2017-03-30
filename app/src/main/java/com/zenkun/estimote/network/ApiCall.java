package com.zenkun.estimote.network;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.zenkun.estimote.model.ModelVenue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zen zenyagami@gmail.com on 23/02/2017.
 */

public class ApiCall {

    private static final String API_KEY="USE_YOUR_API_KEY";
    private static final String GOOGLE_SEARCH_PLACE_URL ="https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static final String GOOGLE_PHOTO_URL ="https://maps.googleapis.com/maps/api/place/photo?maxwidth=1080&maxheight=720&photoreference=%s&key=%s";
    //i used google places because we can use chunks of data..
    private static final String GOOGLE_SEARCH_PLACE_NEXT_PAGE ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=%s&key=%s";

    private static String nextPage=null;

    //TODO maake this in chunks maybe...
    //TODO: separate HTTP calls from the "data object calls" , getVenueList in this case...
    public static ArrayList<ModelVenue> getVenueList(LatLng latLng,int index) throws Exception {
        //TODO: validate if latLng is null

        //if we get index ==0 we set nextPage to null
        if(index==0)
            nextPage=null;

        //no more page to load
        if(TextUtils.isEmpty(nextPage)&& index>0)
            return null;

        HashMap<String,String> nameValuePairs = new HashMap<>();
        if(TextUtils.isEmpty(nextPage))
        {
            nameValuePairs.put("location",String.format("%s,%s",latLng.latitude,latLng.longitude));
            nameValuePairs.put("radius", String.valueOf("50000"));
        }else
        {
            nameValuePairs.put("pagetoken",nextPage);
        }
        nameValuePairs.put("key", API_KEY);


        JSONObject response =NetClient.getJsonFromServer(GOOGLE_SEARCH_PLACE_URL,nameValuePairs);
        if(response==null)
        {
            throw  new Exception("Check your internet connextion");
        }
        JSONArray array = response.getJSONArray("results");
        //max 4 results
        int size = array.length();
        int counter =index;
        ArrayList<ModelVenue> venueList = new ArrayList<>();
        nextPage=response.optString("next_page_token");
        for (int i=0;i<size;i++)
        {

            try {

                if(counter>100)break;

                JSONObject data = array.getJSONObject(i);

                ModelVenue venue = new ModelVenue();
                venue.latLng = new LatLng(data.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                        data.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));

                venue.venueName=data.getString("name");
                if(data.has("photos"))
                {
                    venue.photoReferenceUrl= String.format(GOOGLE_PHOTO_URL,data.getJSONArray("photos").getJSONObject(0).getString("photo_reference"),
                            API_KEY);
                }else
                {
                    venue.photoReferenceUrl = data.getString("icon");
                }
                venueList.add(venue);
                counter++;
            }catch (Exception ex){ex.printStackTrace();}
        }


        return venueList;

    }
}
