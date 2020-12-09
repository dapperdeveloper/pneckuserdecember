package com.callpneck.commonutility;

import android.app.ProgressDialog;
import android.content.Context;

public class Utility {

    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Context Activity) {
        progressDialog = new ProgressDialog(Activity);
        progressDialog.setTitle("Please wait....");
        progressDialog.show();
    }


    public static void dismissProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
