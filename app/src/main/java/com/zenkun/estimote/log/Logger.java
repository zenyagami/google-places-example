package com.zenkun.estimote.log;

import android.util.Log;

/**
 * Created by Zen zenyagami@gmail.com on 23/02/2017.
 */

public class Logger {
    private static final String TAG = "beacon";

    public static void v(String message)
    {
        log(message);
    }

    private static void log(String message) {
        Log.v(TAG,message);
    }
}
