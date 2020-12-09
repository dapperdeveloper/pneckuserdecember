package com.callpneck.commonutility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommonUtility {

    //    checking Network
    public static boolean isNetworkAvailable(Context con) {
        boolean tempReturn = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                tempReturn = true;
            } else {
//                GlobalAlerts.ShowNetworkError(con);
                tempReturn = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempReturn;
    }
}
