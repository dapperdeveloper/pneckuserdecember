package com.callpneck.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.callpneck.R;


public class BaseActivity extends AppCompatActivity {
    ProgressDialog progressDialog;

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "Speech Audio";
    private String FIELD = "";
    private static final int AUDIO_RECORD_PERMISSION = 100;
    public static String AUDIO_MESSAGE = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        hideKeyPad();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading.....");
        progressDialog.setCancelable(false);
    }

    public void showDialog() {

        try {
            if (progressDialog != null) {
                progressDialog.show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hideDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {

                progressDialog.dismiss();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void setStatusBarColor() {
        try{
            Window window = getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public void hideKeyPad() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showKeyPad() {
        View view = getCurrentFocus();
        if (view != null) {
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }



//    public void pushFragment(Fragment fragment, String tag){
//        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.frame_layout, fragment,tag);
//        ft.addToBackStack(tag);
//        ft.commit();
//    }
    public  void showInternetConnectionError(){
        Toast.makeText(this,getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
    }
    public void showToast(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }

}
