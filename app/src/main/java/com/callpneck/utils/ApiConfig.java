package com.callpneck.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Model.GetWallet;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;
import com.callpneck.activity.registrationSecond.helper.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

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
        Call<JsonObject> call = APIClient.getInstance().getWallet(session.getUserid());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(new Gson().toJson(response.body()));

                    Constant.WALLET_BALANCE = jsonObject.getDouble("amount");

                }catch (Exception e){
                    e.toString();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("WalletBalance", t.getMessage());
            }
        });
        return Constant.WALLET_BALANCE;
    }
}
