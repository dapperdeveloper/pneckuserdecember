package com.callpneck.activity.registrationSecond.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.Database.MainData;
import com.callpneck.activity.Database.RoomDB;
import com.callpneck.activity.registrationSecond.Adapter.MyRestaurantMenuAdapter;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.productListResponse.ProductList;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.productListResponse.ShopDataList;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopDetailActivity extends AppCompatActivity {


    TextView shopNameTv, ratingsTv, dTimeTv, offerLabelTv;
    String shopName, shopAddress,  rating, dTime, discount, discountMin;
    ImageButton cartBtn;
    //for test only
    NotificationBadge notification_badge;
    RecyclerView restaurantMenuRv;
    List<ProductList> itemList;
    MyRestaurantMenuAdapter adapter;
    private SessionManager sessionManager;

    private TextView cartCountTv, nodata;

    String res_id = "";
    String address;


    RoomDB database;
    List<MainData> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_retaurant_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        notification_badge = findViewById(R.id.notification_badge);
        restaurantMenuRv = findViewById(R.id.restaurantMenuRv);


        shopNameTv = findViewById(R.id.shopNameTv);
        ratingsTv = findViewById(R.id.ratingsTv);
        dTimeTv = findViewById(R.id.dTimeTv);
        offerLabelTv = findViewById(R.id.offerLabelTv);
        cartCountTv = findViewById(R.id.cartCountTv);
        cartBtn = findViewById(R.id.cartBtn);
        nodata = findViewById(R.id.nodata);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

        sessionManager = new SessionManager(this);
        //store database value in data list
        //Initialize database
        database = RoomDB.getInstance(this);
//        dataList = database.mainDao().getAll();

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);

        if (getIntent() != null){
            shopName = getIntent().getStringExtra("shopName");
            shopAddress = getIntent().getStringExtra("shopAddress");
            rating = getIntent().getStringExtra("ratings");
            dTime = getIntent().getStringExtra("dTime");
            discount = getIntent().getStringExtra("discount");
            discountMin = getIntent().getStringExtra("discountMin");
            res_id = getIntent().getStringExtra("res_id");
            shopNameTv.setText(shopName);
            ratingsTv.setText(rating);
            dTimeTv.setText(dTime+" Mins");

            if (discount.equalsIgnoreCase("0")){
                offerLabelTv.setVisibility(View.GONE);
            }
            else {
                offerLabelTv.setVisibility(View.VISIBLE);
                offerLabelTv.setText("Get "+discount + "% off on order above Rs."+discountMin);
            }



        }


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0){
                    collapsingToolbarLayout.setTitle(shopName);
                    isShow = false;
                }else{
                    collapsingToolbarLayout.setTitle("");
                    isShow = true;
                }
            }
        });


        cartCount();

        itemList = new ArrayList<>();

        if (AppController.isConnected(ShopDetailActivity.this))
       getProductList();


       findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               onBackPressed();
           }
       });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditCartActivity();
            }
        });

        notification_badge.setText(String.valueOf(10));
    }

    public void cartCount() {
        //store database value in data list
        dataList = database.mainDao().getAll();
        int count = dataList.size();
        if(count<=0){
            cartCountTv.setVisibility(View.GONE);
        }else {
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText(""+count);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //store database value in data list
        dataList = database.mainDao().getAll();
    }

    private void openEditCartActivity() {
        Intent intent = new Intent(ShopDetailActivity.this, EditCartActivity.class);
        intent.putExtra("res_id",res_id);
        intent.putExtra("shopName",shopName);
        intent.putExtra("shopAddress", shopAddress);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);

    }

    private void getProductList() {
        Call<ShopDataList> call = APIClient.getInstance().getProductList(res_id);
        call.enqueue(new Callback<ShopDataList>() {
            @Override
            public void onResponse(Call<ShopDataList> call, Response<ShopDataList> response) {
                ShopDataList model = response.body();
                if (model != null && model.getSuccess() && model.getData().size()>0){
                    itemList.clear();
                    itemList = model.getData();
                    adapter = new MyRestaurantMenuAdapter(ShopDetailActivity.this, itemList);
                    restaurantMenuRv.setAdapter(adapter);
                    nodata.setVisibility(View.GONE);
                }
                else {
                    if (model != null && model.getData().size()==0){
                        Toast.makeText(ShopDetailActivity.this, "Product Not Found", Toast.LENGTH_SHORT).show();
                        nodata.setVisibility(View.VISIBLE);
                    }
                    else {
                        Toast.makeText(ShopDetailActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                        nodata.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ShopDataList> call, Throwable t) {

                Log.d("TahseenKhan",t.getMessage());
                nodata.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (dataList.size()>0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("All item in cart will be delete.");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //delete all data from database
                    database.mainDao().reset(dataList);
                    //notify when all data is deleted
                    dataList.clear();
                    dataList.addAll(database.mainDao().getAll());
                    onBackPressed();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create();
            builder.show();
        }
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);
        }
    }
}