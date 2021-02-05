package com.callpneck.activity.registrationSecond.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.Database.MainData;
import com.callpneck.activity.Database.RoomDB;
import com.callpneck.activity.registrationSecond.Adapter.MyProviderAdapter;
import com.callpneck.activity.registrationSecond.Model.response.responseCategoryList.ModelProvider;
import com.callpneck.activity.registrationSecond.Model.response.responseCategoryList.Vendor;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;
import com.google.android.material.snackbar.Snackbar;
import com.muddzdev.styleabletoast.StyleableToast;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

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
    RoomDB database;
    List<MainData> dataList = new ArrayList<>();
    private ImageView noDataImage;
    private RelativeLayout nodataLayout;
    private  TextView messageTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_provider);
        providerListRv = findViewById(R.id.providerListRv);
        titleTv = findViewById(R.id.titleTv);
        progressBar = findViewById(R.id.progress_bar);
        noDataImage = findViewById(R.id.noDataImage);
        nodataLayout = findViewById(R.id.nodataLayout);
        messageTv = findViewById(R.id.messageTv);


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
        //Initialize database
        database = RoomDB.getInstance(this);
        //store database value in data list
        dataList = database.mainDao().getAll();
        if (dataList != null)
            deleteItemFromCart();
        //init permission
        user_id = sessionManager.getUserid();
        ep_token = sessionManager.getUserToken();
        curr_lat = sessionManager.getUserLatitude();
        curr_long =  sessionManager.getUserLongitude();

        Glide.with(this).load(R.drawable.no_data_found).into(noDataImage);
        if (AppController.isConnected(ProviderActivity.this))
            if (validation())
            getProviderDetail(user_id, ep_token, curr_lat,curr_long);
    }
    private void deleteItemFromCart() {
        //delete all data from database
        database.mainDao().reset(dataList);
        //notify when all data is deleted
        dataList.clear();
        dataList.addAll(database.mainDao().getAll());

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
        Log.d("TahseenVendorList","user_id-"+ user_id +"ep_token-"+ ep_token +"longitude-"+ curr_long +"latitute-"+ curr_lat +"category-"+category);
        Call<ModelProvider> call = APIClient.getInstance().vendorList(user_id,ep_token, curr_lat, curr_long, category);
        call.enqueue(new Callback<ModelProvider>() {
            @Override
            public void onResponse(Call<ModelProvider> call, Response<ModelProvider> response) {
                if (response.isSuccessful()){
                    try {
                        ModelProvider modelProvider = response.body();
                        if (modelProvider.getResponse() != null && modelProvider.getResponse().getSuccess()){
                            providerList.clear();
                            providerList = modelProvider.getResponse().getData().getVendors();
                            if (providerList.size()>0){
                                adapter = new MyProviderAdapter(ProviderActivity.this, providerList, new MyProviderAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Vendor item) {
                                        if (item.getType().equals("dynamic")){
                                            Intent intent = new Intent(ProviderActivity.this, ServiceDetailActivity.class);
                                            intent.putExtra("shopId", item.getVendorId()+"");
                                            intent.putExtra("shopName", item.getShopTitle()+"");
                                            intent.putExtra("shopAvatar", item.getImage()+"");
                                            intent.putExtra("shopRating", item.getRating()+"");
                                            intent.putExtra("shopAddress", item.getCurrLocAddress()+"");
                                            intent.putExtra("categoryName", category);
                                            startActivity(intent);
                                        }
                                        else {
                                            Intent intent = new Intent(ProviderActivity.this, ProviderDetailActivity.class);
                                            intent.putExtra("shopId", item.getVendorId()+"");
                                            intent.putExtra("shopName", item.getShopTitle()+"");
                                            intent.putExtra("shopAvatar", item.getImage()+"");
                                            intent.putExtra("shopRating", item.getRating()+"");
                                            intent.putExtra("shopAddress", item.getCurrLocAddress()+"");
                                            intent.putExtra("categoryName", category);
                                            startActivity(intent);
                                        }

                                    }
                                });
                                providerListRv.setAdapter(adapter);
                                progressBar.setVisibility(View.GONE);
                                nodataLayout.setVisibility(View.GONE);
                            }else {
                                progressBar.setVisibility(View.GONE);
                                nodataLayout.setVisibility(View.VISIBLE);
                                messageTv.setText(modelProvider.getResponse().getMessage());
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        nodataLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }


            }

            @Override
            public void onFailure(Call<ModelProvider> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                nodataLayout.setVisibility(View.VISIBLE);
                showSnackBar(ProviderActivity.this, t.getMessage());
            }
        });



    }



    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}