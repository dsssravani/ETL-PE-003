package com.example.myapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtility {

    //Constant used for Logs
    private static final String LOG_TAG = NetworkUtility.class.getSimpleName();
    public static boolean isNetworkConnected(Context context) {
        //Retrieving the Connectivity Manager from the Context
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Flag to save the connectivity status
        boolean isNetworkConnected = false;

        if (connectivityManager != null) {
            //Retrieving current active default data network
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            //Retrieving the connectivity status
            isNetworkConnected = (activeNetworkInfo != null && activeNetworkInfo.isConnected());
        }

        //Returning the state of Internet Connectivity
        return isNetworkConnected;
    }

}
