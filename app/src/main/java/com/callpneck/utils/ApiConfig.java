package com.callpneck.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Model.GetWallet;
import com.callpneck.activity.registrationSecond.api.ApiClient;
import com.callpneck.activity.registrationSecond.helper.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConfig {


    public static final String TRIPSTATUSTYPE ="TRIPSTATUSTYPE" ;
    public static final String ACTIVELOADTYPE = "ACTIVELOADTYPE";
    public static final String USERFIREBASEID = "USERFIREBASEID";
    public static final String CHATUSERID ="CHATUSERID" ;


    public static String sharedpreffilename="Roadwaysgenius";
    public static String MOVERUSERID="moveruserid";
    public static String CHECKDATA="CHECKDATA";
    public static String USERNOTE="USERNOTE";
    public static String PICKUPPOSTSTATUSTYPE="PICKUPPOSTSTATUSTYPE";
    public static String LOGINTYPE="LOGINTYPE";


    static Boolean okDialogbutton  = false;
    public static String LOGINBY="LOGINBY";
    public static String LOGINFACEBOOK="facebook";
    public static String APIRESPONSE="APIRESPONSE";
    public static String baseurl="http://pneck.in//api/";


    public static ApiInterface retrofitRegister()
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseurl).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
        ApiInterface requestInterface = retrofit.create(ApiInterface.class);
        return  requestInterface;
    }

    // Method to manually check connection status
 /*   public static void checkConnection(View view) {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected,view);
    }*/

    // Showing the status in Snackbar


   public static void hideKeyboardlat(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static double getWalletBalance(final Activity activity, SessionManager session) {

        com.callpneck.activity.registrationSecond.api.ApiInterface apiInterface = ApiClient.getInstance(activity).getApi();
        Call<GetWallet> call = apiInterface.getWallet(session.getUserid());
        call.enqueue(new Callback<GetWallet>() {
            @Override
            public void onResponse(Call<GetWallet> call, Response<GetWallet> response) {
               try {
                   GetWallet getWallet = response.body();
                   if (getWallet != null && getWallet.getStatus()){
                       Constant.WALLET_BALANCE = Double.parseDouble(getWallet.getAmount()+"");
//                       tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
                   }
               }catch (Exception e){
                   e.printStackTrace();
               }

            }

            @Override
            public void onFailure(Call<GetWallet> call, Throwable t) {
                Log.d("WalletBalance", t.getMessage());
            }
        });
        return Constant.WALLET_BALANCE;
    }
}
