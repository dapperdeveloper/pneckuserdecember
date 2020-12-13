package com.callpneck.activity.registrationSecond.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.registrationSecond.Adapter.MyProviderAdapter;
import com.callpneck.activity.registrationSecond.Model.response.responseCategoryList.ModelProvider;
import com.callpneck.activity.registrationSecond.Model.response.responseCategoryList.Vendor;
import com.callpneck.activity.registrationSecond.api.ApiClient;
import com.callpneck.activity.registrationSecond.api.ApiInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderActivity extends AppCompatActivity {

    private RecyclerView providerListRv;
    List<Vendor> providerList;
    MyProviderAdapter adapter;
    TextView  titleTv;

    String user_id, ep_token, curr_lat, curr_long, category;
    SessionManager sessionManager;
    private AVLoadingIndicatorView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_provider);
        providerListRv = findViewById(R.id.providerListRv);
        titleTv = findViewById(R.id.titleTv);
        progressBar = findViewById(R.id.progress_bar);


        if (getIntent() != null){
            category = getIntent().getStringExtra("categoryName");
            titleTv.setText(category);
        }

        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sessionManager = new SessionManager(this);
        //init permission

        user_id = sessionManager.getUserid();
        ep_token = sessionManager.getUserToken();
        curr_lat = sessionManager.getUserLatitude();
        curr_long =  sessionManager.getUserLongitude();

        if (AppController.isConnected(ProviderActivity.this))
            if (validation())
            getProviderDetail(user_id, ep_token, curr_lat,curr_long);
    }

    private boolean validation(){
        boolean valid = true;
        if(TextUtils.isEmpty(curr_lat)){
            showSnackBar(ProviderActivity.this, "Location not set yet!");
            valid = false;
        }
        else if(TextUtils.isEmpty(curr_long)){
            showSnackBar(ProviderActivity.this, "Location not set yet!");
            valid = false;
        }

        return valid;
    }
    private  void showSnackBar(Activity activity, String snackTitle) {
        View Parentview=activity.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(Parentview, snackTitle, Snackbar.LENGTH_SHORT);
        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
    }
    private void getProviderDetail(String user_id, String ep_token, String curr_lat, String curr_long) {
        providerList = new ArrayList<>();
        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Log.d("TahseenVendorList","user_id-"+ user_id +"ep_token-"+ ep_token +"longitude-"+ curr_long +"latitute-"+ curr_lat +"category-"+category);
        Call<ModelProvider> call = apiInterface.vendorList(user_id,ep_token, curr_lat, curr_long, category);
        call.enqueue(new Callback<ModelProvider>() {
            @Override
            public void onResponse(Call<ModelProvider> call, Response<ModelProvider> response) {

                try {
                    ModelProvider modelProvider = response.body();
                    if (modelProvider.getResponse() != null){
                        providerList.clear();
                        providerList = modelProvider.getResponse().getData().getVendors();
                        if ( modelProvider.getResponse().getData().getVendors().size()>0)
                            adapter = new MyProviderAdapter(ProviderActivity.this, providerList, new MyProviderAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Vendor item) {
                                    if (item.getType().equals("dynamic")){
                                        Intent intent = new Intent(ProviderActivity.this, ServiceDetailActivity.class);
                                        intent.putExtra("shopId", item.getVendorId()+"");
                                        intent.putExtra("shopName", item.getShopTitle()+"");
                                        intent.putExtra("shopAvatar", item.getImage()+"");
                                        intent.putExtra("shopRating", item.getRating()+"");
                                        startActivity(intent);
                                    }
                                    else {
                                        Intent intent = new Intent(ProviderActivity.this, ProviderDetailActivity.class);
                                        intent.putExtra("shopId", item.getVendorId()+"");
                                        intent.putExtra("shopName", item.getShopTitle()+"");
                                        intent.putExtra("shopAvatar", item.getImage()+"");
                                        intent.putExtra("shopRating", item.getRating()+"");
                                        startActivity(intent);
                                    }

                                }
                            });
                        providerListRv.setAdapter(adapter);
                        showSnackBar(ProviderActivity.this, modelProvider.getResponse().getMessage());
                        progressBar.setVisibility(View.GONE);
                    }
                    else{
                        if (modelProvider != null && !modelProvider.getResponse().getSuccess()){
                            showSnackBar(ProviderActivity.this, modelProvider.getResponse().getMessage());
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            showSnackBar(ProviderActivity.this, "Server Error");
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ModelProvider> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showSnackBar(ProviderActivity.this, t.getMessage());
            }
        });



    }



    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

    }
}