package com.zenkun.estimote.network;

import com.google.android.gms.maps.model.LatLng;
import com.zenkun.estimote.model.ModelVenue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class NetClient {
    public static JSONObject getJsonFromServer(String baseUrl, HashMap<String, String> nameValuePairs) {
        URL url;
        HttpURLConnection urlConnection = null;

        String query = getQuery(nameValuePairs);

        try {
            url = new URL(baseUrl + (query == null ? "" : ("?" + query)));
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String parseString = streamToString(in);
          //  JSONObject js = new JSONObject();
            try {
                return new JSONObject(parseString);
            } catch (Exception e) {
                return null;
            }
            //return new JSONObject(parseString);
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if(urlConnection!=null)
                urlConnection.disconnect();

        }
        return null;
    }

    private static String streamToString(InputStream is){
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext()?s.next():"";
    }

    private static String getQuery(HashMap<String,String> params)
    {
        if(params==null)return null;

        StringBuilder result = new StringBuilder();
        boolean first = true;

        try {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(String.valueOf(pair.getKey()), "UTF-8"));
                result.append("=");
                if(pair.getValue()!=null)
                    result.append(URLEncoder.encode(String.valueOf(pair.getValue()), "UTF-8"));
                else
                    result.append("");

               // System.out.println(pair.getKey() + " = " + pair.getValue());
            }
            return result.toString();
        }catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }


}
