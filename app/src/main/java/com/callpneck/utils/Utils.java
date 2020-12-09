package com.callpneck.utils;

/**
 * Created by ankit on 11/25/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;


import com.callpneck.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

    private AlertDialog alertDialog;
    public static Context appContext;

    public Utils(Context applicationContext) {
        this.appContext=applicationContext;
    }


    public void Hotline_dialog(final Activity mContext, String message, final int position) {
        final DialogListener mDialogListener
         = (DialogListener) mContext;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        alertDialogBuilder.setMessage(message);


        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                mDialogListener.onPressOk(position, "");
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                alertDialog.dismiss();
                mDialogListener.onPressCancel(position, "");


            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public static void shareApp(Context context)
    {
        final String appPackageName = context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out Aap Ka Sewak App at: https://play.google.com/store/apps/details?id=" + appPackageName);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public static void HideKeyPad(Activity mActivity) {


        View view = mActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static String getCaps(String letter) {
        return letter.toUpperCase();
    }

    public static String removeWitheSpaceFromUrl(String url, int width) {
        String urlStr = url.replace("width", "10000").replace("height", String.valueOf(width));
        urlStr = urlStr.replaceAll(" ", "%20");
        Log.d("urlStr", urlStr);
        return urlStr;
    }
    public static String convert_string(String ss) {
        if (ss.equals(null) || ss.equals("null") || ss == null) {
            return "";
        } else {
            return ss;
        }

    }


    public static void showLog(String TAG, String message){
    }

    public static void showTost(Context context, String msg){
        Toast.makeText(context,msg, Toast.LENGTH_LONG).show();
    }

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }


    public static String getCtmStoreImageUrl(String url){
        return "https://ctmstorage.blob.core.windows.net/testctmstore/"+url;
//return url;
    }

    public static void loadEblaaImage(Context context, String url, ImageView imageView){
        if (TextUtils.isEmpty(url))
            return;

        //Picasso.with(context).load(url).placeholder(R.mipmap.image_placeholder).into(imageView);
    }
    public static String getEpsiodImageUrl(String url){
        return "https://ctmstorage.blob.core.windows.net/testctmepisodes/"+url;
//        return url;
    }
    public static JSONObject getBasicJson(){
//        JSONObject obj=new JSONObject();
        JSONObject detail=new JSONObject();
        try {
            detail.put("ServiceAuthenticationToken","4CB55B60-5A30-4984-A8BB-CFEE63E1B488");
            detail.put("UserLoginAuthenticationKey","");
            detail.put("LoggedInUserId","");
            detail.put("setLoggedInUserId","");
            detail.put("setClientBrowserTimeOffSet","");
            detail.put("LanguageTypeId",1);
            detail.put("setClientBrowserTimeZone","");
//
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return detail;
    }

    public static void setApplicaionContext(Context mContext){
        appContext=mContext;
    }
    public static Context getApplicationContext(){
        return appContext;
    }
}