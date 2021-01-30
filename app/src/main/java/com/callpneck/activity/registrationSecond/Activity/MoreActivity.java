package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.deliveryboy.DeliveryMainActivity;
import com.callpneck.activity.registrationSecond.Adapter.MyCategoryAdapter;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.model.dashboard.MainDashboard;
import com.callpneck.model.dashboard.SubcategoryList;
import com.callpneck.taxi.TaxiMainActivity;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyCategoryAdapter adapter;
    MainDashboard mainDashboard;

    private SessionManager sessionManager;

    private AVLoadingIndicatorView progressBar;
    private SwipeRefreshLayout swipeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        swipeLayout = findViewById(R.id.swipeLayout);
        sessionManager = new SessionManager(this);

        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initView();
        if (AppController.isConnected(this))
            getData();

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }
    private void initView() {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
//        recyclerView.addItemDecoration(new SpaceItemDecoration(0));
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        Call<MainDashboard> mainDashboardCall = APIClient.getInstance()
                .getDashFullData();
        mainDashboardCall.enqueue(new Callback<MainDashboard>() {
            @Override
            public void onResponse(Call<MainDashboard> call, Response<MainDashboard> response) {
                if (response.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    try {
                        mainDashboard = response.body();
                        if (mainDashboard.getResponse().getData().getSubcategoryList().size()>0 && mainDashboard != null){
                            loadCategoryView(mainDashboard.getResponse().getData().getSubcategoryList());
                        }

                    }catch (Exception e){

                    }
                }


            }

            @Override
            public void onFailure(Call<MainDashboard> call, Throwable t) {

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadCategoryView(List<SubcategoryList> subcategoryList) {
        adapter = new MyCategoryAdapter(this, subcategoryList, new MyCategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SubcategoryList item) {
                if (item.getCate_type().equalsIgnoreCase("restaurant")) {
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null){
                        Intent intent = new Intent(MoreActivity.this, ShopHomeActivity.class);
                        intent.putExtra("categoryName", item.getTitle());
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                    }


                } else if(item.getCate_type().equalsIgnoreCase("cab")){
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                        Intent intent = new Intent(MoreActivity.this, TaxiMainActivity.class);
                        intent.putExtra("categoryName", item.getTitle());
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                    }
                }
                else if(item.getCate_type().equalsIgnoreCase("provider")){
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                        Intent intent = new Intent(MoreActivity.this, ProviderDetailActivity.class);
                        intent.putExtra("categoryName", item.getTitle());
                        startActivity(intent);
                       overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                    }
                }
                else if(item.getCate_type().equalsIgnoreCase("shop")){
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                        Intent intent = new Intent(MoreActivity.this, ProviderActivity.class);
                        intent.putExtra("categoryName", item.getTitle());
                        startActivity(intent);
                      overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                    }
                }
                else if(item.getCate_type().equalsIgnoreCase("delivery")){
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {

                        if (sessionManager.getCurrentDeliveryOrderId()!=null&&
                                sessionManager.getCurrentDeliveryOrderId().length()>0){
                            LaunchActivityClass.LaunchTrackingDeliveryScreen(MoreActivity.this);
                        }
                        else {
                            Intent intent = new Intent(MoreActivity.this, DeliveryMainActivity.class);
                            intent.putExtra("categoryName", item.getTitle());
                            startActivity(intent);
                           overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                        }

                    }
                }
                else if(item.getCate_type().equalsIgnoreCase("wallet")){
                    Intent intent = new Intent(MoreActivity.this, MyWalletActivity.class);
                    startActivity(intent);
                   overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                }
                else if(item.getCate_type().equalsIgnoreCase("more")){
                    Intent intent = new Intent(MoreActivity.this, MoreActivity.class);
                    startActivity(intent);
                   overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                }
                else {
                    Toast.makeText(MoreActivity.this, "Location not set", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}