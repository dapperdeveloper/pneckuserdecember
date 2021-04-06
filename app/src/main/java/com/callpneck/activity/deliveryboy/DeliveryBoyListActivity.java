package com.callpneck.activity.deliveryboy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.deliveryboy.Adapter.BoyListAdapter;
import com.callpneck.activity.deliveryboy.model.DUser;
import com.callpneck.activity.deliveryboy.model.DriverList;
import com.callpneck.activity.deliveryboy.model.DriverList_;
import com.callpneck.activity.deliveryboy.model.OrderSubmit;
import com.callpneck.activity.registrationSecond.Activity.CheckoutShopActivity;
import com.callpneck.activity.registrationSecond.Activity.OrderPlacedActivity;
import com.callpneck.activity.registrationSecond.MainScreenActivity;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.api.ApiClient;
import com.callpneck.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import net.bohush.geometricprogressview.GeometricProgressView;

import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryBoyListActivity extends AppCompatActivity {

    BoyListAdapter adapter;
    RecyclerView recyclerView;
    List<DriverList_> dUserList;
    ProgressBar progressbar;
    Toolbar toolbar;
    SwipeRefreshLayout swipeLayout;
    TextView tvAlert;
    String pickupAddress, dropAddress, UserLatitude, UserLongitude;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_delivery_boy_list);

        if (getIntent()!= null){
            pickupAddress = getIntent().getStringExtra("pickupAddress");
            dropAddress = getIntent().getStringExtra("dropAddress");
            UserLatitude = getIntent().getStringExtra("UserLatitude");
            UserLongitude = getIntent().getStringExtra("UserLongitude");

        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Delivery Boy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeLayout = findViewById(R.id.swipeLayout);
        progressbar = findViewById(R.id.progressBar);
        tvAlert = findViewById(R.id.tvAlert);

        sessionManager = new SessionManager(this);
        recyclerView = findViewById(R.id.recycleview);
        if (UserLongitude!= null&& UserLatitude!=null)
            getDeliveryBoyData();
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (dUserList.size()>0)
                    dUserList.clear();
                getDeliveryBoyData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    String deliveryFee="";
    private void getDeliveryBoyData() {
        dUserList = new ArrayList<>();
        Call<DriverList> call = APIClient.getInstance().getDeliveryBoyData(UserLatitude, UserLongitude);
        call.enqueue(new Callback<DriverList>() {
            @Override
            public void onResponse(Call<DriverList> call, Response<DriverList> response) {
                if (response.isSuccessful()){
                    progressbar.setVisibility(View.GONE);
                    try {

                        DriverList model = response.body();
                        if (model!= null && model.getSuccess()){
                            if (model.getDriverList().size()>0){
                                dUserList.clear();
                                dUserList = model.getDriverList();
                                adapter = new BoyListAdapter(dUserList, DeliveryBoyListActivity.this, new BoyListAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(DriverList_ item) {
                                        String distance = item.getDistance()+"";
                                        String km = distance.substring(0,3);

                                        int rateKm = Integer.parseInt(distance.substring(0,1));
                                        if (rateKm==0){
                                            deliveryFee= "30";
                                        }
                                        else if (rateKm == 1){
                                            deliveryFee= "30";
                                        }
                                        else if (rateKm == 2){
                                            deliveryFee = "40";
                                        }
                                        else if (rateKm == 3){
                                            deliveryFee = "50";
                                        }
                                        else if(rateKm == 4){
                                            deliveryFee = "60";
                                        }
                                        else {
                                            deliveryFee = "100";
                                        }

                                        startActivity(new Intent(DeliveryBoyListActivity.this, CreateOrderActivity.class).putExtra("emp_id", item.getId()+"").putExtra("emp_name", item.getFirstName()+" "+item.getLastName()+"").putExtra("emp_address",item.getEmpAddress()+"").putExtra("deliveryFee",deliveryFee).putExtra("pickupAddress", pickupAddress).putExtra("dropAddress",dropAddress));
                                    }
                                });
                                recyclerView.setAdapter(adapter);
                            }

                        }

                    }catch (Exception e){
                        e.toString();
                    }
                }
            }

            @Override
            public void onFailure(Call<DriverList> call, Throwable t) {
                progressbar.setVisibility(View.GONE);
            }
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);
    }
}