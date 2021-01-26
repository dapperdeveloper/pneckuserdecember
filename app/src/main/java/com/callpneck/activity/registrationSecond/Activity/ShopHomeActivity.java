package com.callpneck.activity.registrationSecond.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.Database.MainData;
import com.callpneck.activity.Database.RoomDB;
import com.callpneck.activity.registrationSecond.Adapter.AdapterAutoSliderExample;
import com.callpneck.activity.registrationSecond.Adapter.MyRestaurantListAdapter;
import com.callpneck.activity.registrationSecond.Adapter.MyShopAdapter;
import com.callpneck.activity.registrationSecond.Model.FoodShop;
import com.callpneck.activity.registrationSecond.Model.bannerData.BannerDataResponse;
import com.callpneck.activity.registrationSecond.Model.bannerData.Datum;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.Cuisines;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ProductResponse.ProductFood;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ProductResponse.ProductResponse;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ResponseFoodHome;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;
import com.callpneck.model.ModelSliderMain;
import com.callpneck.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
import com.muddzdev.styleabletoast.StyleableToast;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopHomeActivity extends AppCompatActivity {

    String categoryName;

    TextView titleTv, filterTv, addressTv;
    String title;
    private EditText searchView;
    RecyclerView shopRv;
    List<FoodShop> shopList;
    List<ProductFood> productFoods;
    MyShopAdapter shopAdapter;



    private AVLoadingIndicatorView  progressDialog;
    LinearLayout cuisinesLayout;

    private SessionManager sessionManager;

    RoomDB database;
    List<MainData> dataList = new ArrayList<>();

    String latitude, longitude;
    SwipeRefreshLayout swipeLayout;

    AdapterAutoSliderExample adapter;
    List<ModelSliderMain> categoryList;
    //sliderLayout
    private SliderView sliderView;

    List<Datum> bannerList;
    CardView bannerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_food_home);

        Intent intent = getIntent();
        if (intent != null){
            categoryName = intent.getStringExtra("categoryName");
        }

        titleTv = findViewById(R.id.titleTv);
        shopRv = findViewById(R.id.shopRv);
        filterTv = findViewById(R.id.filterTv);
        searchView = findViewById(R.id.searchView);
        addressTv = findViewById(R.id.addressTv);
        progressDialog = findViewById(R.id.progress_bar);
        swipeLayout = findViewById(R.id.swipeLayout);
        shopList = new ArrayList<>();
        productFoods = new ArrayList<>();

        sliderView = findViewById(R.id.sliderLayout);
        bannerLayout = findViewById(R.id.bannerLayout);
        categoryList = new ArrayList<>();



        sessionManager = new SessionManager(this);
        latitude = sessionManager.getUserLatitude();
        longitude = sessionManager.getUserLongitude();

        addressTv.setText(sessionManager.getUserScreenAddress());
        if (AppController.isConnected(ShopHomeActivity.this)){
            if (validation()){
                loadShopData();
                loadCategoryData();
            }

            sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
            sliderView.startAutoCycle();

            search();

        }


        //Initialize database
        database = RoomDB.getInstance(this);
        //store database value in data list
        dataList = database.mainDao().getAll();
        if (dataList != null)
            deleteItemFromCart();

        titleTv.setText("All");


        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppController.isConnected(ShopHomeActivity.this)){

                    if (validation()){
                        loadCategoryData();
                        loadShopData();
                    }

                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        filterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setVisibility(View.VISIBLE);
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopHomeActivity.this);
                builder.setTitle("Choose Restaurant:")
                        .setItems(Constants.productsCategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = Constants.productsCategory1[which];

                                if(selected.equals("All")){
                                    if (AppController.isConnected(ShopHomeActivity.this))
                                    {
                                        if (validation())
                                            loadShopData();
                                    }

                                    titleTv.setText(selected);
                                }
                                if(selected.equals("High To Low")){
                                    if (AppController.isConnected(ShopHomeActivity.this))
                                    {
                                        if (validation())
                                            loadShopHighToLowData();
                                    }

                                    titleTv.setText(selected);
                                }
                                if(selected.equals("Offer")){
                                    if (AppController.isConnected(ShopHomeActivity.this))
                                    {
                                        if (validation())
                                        loadShopOfferData();
                                    }
                                    titleTv.setText(selected);
                                }
                                if(selected.equals("Low To High")){
                                    if (AppController.isConnected(ShopHomeActivity.this))
                                    {
                                        if (validation())
                                            loadShopLowToHighData();
                                    }

                                    titleTv.setText(selected);
                                }
                                if(selected.equals("Delivery Time")){
                                    if (AppController.isConnected(ShopHomeActivity.this))
                                    {
                                        if (validation())
                                        loadShopByDeliveryTime();
                                    }

                                    titleTv.setText(selected);
                                }
                                if(selected.equals("Ratings")){
                                    if (AppController.isConnected(ShopHomeActivity.this))
                                    {
                                        if (validation())
                                        loadShopByRatings();
                                    }

                                    titleTv.setText(selected);
                                }
                                if(selected.equals("Relevance")){
                                    if (AppController.isConnected(ShopHomeActivity.this))
                                    {
                                        if (validation())
                                        loadShopByRelevance();
                                    }

                                    titleTv.setText(selected);
                                }


                            }
                        }).show();
            }
        });
    }

    private void loadCategoryData() {
        bannerList = new ArrayList<>();
        Call<BannerDataResponse> call = APIClient.getInstance().getBanner(latitude, longitude);
        Log.e("BannerData", latitude+"\n"+longitude);
        call.enqueue(new Callback<BannerDataResponse>() {
            @Override
            public void onResponse(Call<BannerDataResponse> call, Response<BannerDataResponse> response) {

                if(response.isSuccessful()){
                    try {
                        BannerDataResponse responseFoodHome = response.body();
                        Log.e("BannerData", response.body().getData()+"");
                        if (responseFoodHome!=null){
                            if (responseFoodHome.getErrorCode()==0 && responseFoodHome.getData().size() > 0){
                                bannerLayout.setVisibility(View.VISIBLE);
                                bannerList.clear();
                                bannerList = responseFoodHome.getData();
                                adapter= new AdapterAutoSliderExample(ShopHomeActivity.this,bannerList);
                                sliderView.setSliderAdapter(adapter);
                            }
                        }

                    }catch (Exception e){
                        bannerLayout.setVisibility(View.GONE);
                    }
                }


            }

            @Override
            public void onFailure(Call<BannerDataResponse> call, Throwable t) {
                Log.e("BannerData", t.getMessage()+"");
                bannerLayout.setVisibility(View.GONE);

            }
        });



    }
    private boolean validation(){
        boolean valid = true;
        if(TextUtils.isEmpty(latitude)){
            StyleableToast.makeText(ShopHomeActivity.this, "Location not set yet!", Toast.LENGTH_LONG, R.style.mytoast).show();
            valid = false;
        }
        else if(TextUtils.isEmpty(longitude)){

            StyleableToast.makeText(ShopHomeActivity.this, "Location not set yet!", Toast.LENGTH_LONG, R.style.mytoast).show();
            valid = false;
        }

        return valid;
    }
    private void deleteItemFromCart() {
        //delete all data from database
        database.mainDao().reset(dataList);
        //notify when all data is deleted
        dataList.clear();
        dataList.addAll(database.mainDao().getAll());
    }

    private void search() {

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !editable.toString().trim().isEmpty()){
                    if (AppController.isConnected(ShopHomeActivity.this))
                    getProductDetail(editable.toString());
                    cuisinesLayout.setVisibility(View.GONE);
                }

                else {
                    cuisinesLayout.setVisibility(View.VISIBLE);
                    StyleableToast.makeText(ShopHomeActivity.this, "No Result Found...!", Toast.LENGTH_LONG, R.style.mytoast).show();

                }
            }
        });
    }

    private void getProductDetail(String name) {

        RequestBody requestResId  = RequestBody.create(MediaType.parse("multipart/form-data"), name);
        Call<ProductResponse> call = APIClient.getInstance().restaurantSearchByName(requestResId);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {

                try {
                    productFoods.clear();
                    ProductResponse body = response.body();
                    productFoods= body.getProductFoodList();

                    if (body.getError_code()==0 && productFoods.size() > 0)
                        shopAdapter = new MyShopAdapter(ShopHomeActivity.this,productFoods, new MyShopAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(ProductFood item) {
                                if (item.getIsopen().equalsIgnoreCase("1")){
                                    Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                    intent.putExtra("shopName",item.getName());
                                    intent.putExtra("shopAvatar",item.getImage()+"");
                                    intent.putExtra("shopAddress",item.getAddress());
                                    intent.putExtra("ratings", item.getRating());
                                    intent.putExtra("res_id", item.getId()+"");
                                    intent.putExtra("dTime", item.getDeliveryTime());
                                    intent.putExtra("discount", item.getDiscount());
                                    intent.putExtra("discountMin", item.getDiscountMin());
                                    intent.putExtra("description", item.getDescription());
                                    intent.putExtra("phoneOne", item.getPhone());
                                    intent.putExtra("phoneTwo", item.getMobile());
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                }
                                else {
                                    StyleableToast.makeText(ShopHomeActivity.this, "This Restaurant not taking any order!", Toast.LENGTH_LONG, R.style.mytoast).show();

                                }


                            }
                        });
                    shopRv.setAdapter(shopAdapter);
                    progressDialog.setVisibility(View.GONE);
                }catch (Exception e){
                    progressDialog.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressDialog.setVisibility(View.GONE);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void loadShopByRelevance() {
        Call<ProductResponse> call = APIClient.getInstance().getOpenProductsByRelevance(latitude, longitude);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                try {
                    productFoods.clear();
                    ProductResponse body = response.body();
                    productFoods= body.getProductFoodList();

                    if (body.getError_code()==0 && productFoods.size() > 0)

                        shopAdapter = new MyShopAdapter(ShopHomeActivity.this,productFoods, new MyShopAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(ProductFood item) {

                                if (item.getIsopen().equalsIgnoreCase("1")){
                                    Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                    intent.putExtra("shopName",item.getName());
                                    intent.putExtra("shopAvatar",item.getImage()+"");
                                    intent.putExtra("shopAddress",item.getAddress());
                                    intent.putExtra("ratings", item.getRating());
                                    intent.putExtra("res_id", item.getId()+"");
                                    intent.putExtra("dTime", item.getDeliveryTime());
                                    intent.putExtra("discount", item.getDiscount());
                                    intent.putExtra("discountMin", item.getDiscountMin());
                                    intent.putExtra("description", item.getDescription());
                                    intent.putExtra("phoneOne", item.getPhone());
                                    intent.putExtra("phoneTwo", item.getMobile());
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                }
                                else {
                                    StyleableToast.makeText(ShopHomeActivity.this, "This Restaurant not taking any order!", Toast.LENGTH_LONG, R.style.mytoast).show();

                                }

                            }
                        });
                    shopRv.setAdapter(shopAdapter);
                    progressDialog.setVisibility(View.GONE);
                }catch (Exception e){
                    progressDialog.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressDialog.setVisibility(View.GONE);
            }
        });
    }

    private void loadShopByRatings() {
        Call<ProductResponse> call = APIClient.getInstance().getOpenProductsByrRating(latitude, longitude);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                try {
                    productFoods.clear();
                    ProductResponse body = response.body();
                    productFoods= body.getProductFoodList();

                    if (body.getError_code()==0 && productFoods.size() > 0)
                        shopAdapter = new MyShopAdapter(ShopHomeActivity.this,productFoods, new MyShopAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(ProductFood item) {

                                if (item.getIsopen().equalsIgnoreCase("1")){
                                    Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                    intent.putExtra("shopName",item.getName());
                                    intent.putExtra("shopAvatar",item.getImage()+"");
                                    intent.putExtra("shopAddress",item.getAddress());
                                    intent.putExtra("ratings", item.getRating());
                                    intent.putExtra("res_id", item.getId()+"");
                                    intent.putExtra("dTime", item.getDeliveryTime());
                                    intent.putExtra("discount", item.getDiscount());
                                    intent.putExtra("discountMin", item.getDiscountMin());
                                    intent.putExtra("description", item.getDescription());
                                    intent.putExtra("phoneOne", item.getPhone());
                                    intent.putExtra("phoneTwo", item.getMobile());
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                }
                                else {
                                    StyleableToast.makeText(ShopHomeActivity.this, "This Restaurant not taking any order!", Toast.LENGTH_LONG, R.style.mytoast).show();

                                }

                            }
                        });
                    shopRv.setAdapter(shopAdapter);
                    progressDialog.setVisibility(View.GONE);
                }catch (Exception e){
                    progressDialog.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressDialog.setVisibility(View.GONE);
            }
        });
    }

    private void loadShopByDeliveryTime() {
        Call<ProductResponse> call = APIClient.getInstance().getOpenProductsByDeliveryTime(latitude, longitude);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                try {
                    productFoods.clear();
                    ProductResponse body = response.body();
                    productFoods= body.getProductFoodList();

                    if (body.getError_code()==0 && productFoods.size() > 0)
                        shopAdapter = new MyShopAdapter(ShopHomeActivity.this,productFoods, new MyShopAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(ProductFood item) {
                                if (item.getIsopen().equalsIgnoreCase("1")){
                                    Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                    intent.putExtra("shopName",item.getName());
                                    intent.putExtra("shopAvatar",item.getImage()+"");
                                    intent.putExtra("shopAddress",item.getAddress());
                                    intent.putExtra("ratings", item.getRating());
                                    intent.putExtra("res_id", item.getId()+"");
                                    intent.putExtra("dTime", item.getDeliveryTime());
                                    intent.putExtra("discount", item.getDiscount());
                                    intent.putExtra("discountMin", item.getDiscountMin());
                                    intent.putExtra("description", item.getDescription());
                                    intent.putExtra("phoneOne", item.getPhone());
                                    intent.putExtra("phoneTwo", item.getMobile());
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                }
                                else {
                                    StyleableToast.makeText(ShopHomeActivity.this, "This Restaurant not taking any order!", Toast.LENGTH_LONG, R.style.mytoast).show();

                                }

                            }
                        });
                    shopRv.setAdapter(shopAdapter);
                    progressDialog.setVisibility(View.GONE);
                }catch (Exception e){
                    progressDialog.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressDialog.setVisibility(View.GONE);
            }
        });
    }

    private void loadShopLowToHighData() {
        Call<ProductResponse> call = APIClient.getInstance().getOpenProductsByCostLowToHigh(latitude, longitude);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                try {
                    productFoods.clear();
                    ProductResponse body = response.body();
                    productFoods= body.getProductFoodList();

                    if (body.getError_code()==0 && productFoods.size() > 0)
                        shopAdapter = new MyShopAdapter(ShopHomeActivity.this,productFoods, new MyShopAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(ProductFood item) {

                                if (item.getIsopen().equalsIgnoreCase("1")){
                                    Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                    intent.putExtra("shopName",item.getName());
                                    intent.putExtra("shopAvatar",item.getImage()+"");
                                    intent.putExtra("shopAddress",item.getAddress());
                                    intent.putExtra("ratings", item.getRating());
                                    intent.putExtra("res_id", item.getId()+"");
                                    intent.putExtra("dTime", item.getDeliveryTime());
                                    intent.putExtra("discount", item.getDiscount());
                                    intent.putExtra("discountMin", item.getDiscountMin());
                                    intent.putExtra("description", item.getDescription());
                                    intent.putExtra("phoneOne", item.getPhone());
                                    intent.putExtra("phoneTwo", item.getMobile());
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                }
                                else {
                                    StyleableToast.makeText(ShopHomeActivity.this, "This Restaurant not taking any order!", Toast.LENGTH_LONG, R.style.mytoast).show();

                                }
                            }
                        });
                    shopRv.setAdapter(shopAdapter);
                    progressDialog.setVisibility(View.GONE);
                }catch (Exception e){
                    progressDialog.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressDialog.setVisibility(View.GONE);
            }
        });
    }

    private void loadShopOfferData() {
        Call<ProductResponse> call = APIClient.getInstance().getOpenProductsByOffer(latitude, longitude);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                try {
                    productFoods.clear();
                    ProductResponse body = response.body();
                    productFoods= body.getProductFoodList();
                    if (body.getError_code()==0 && productFoods.size() > 0)
                        shopAdapter = new MyShopAdapter(ShopHomeActivity.this,productFoods, new MyShopAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(ProductFood item) {

                                if (item.getIsopen().equalsIgnoreCase("1")){
                                    Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                    intent.putExtra("shopName",item.getName());
                                    intent.putExtra("shopAvatar",item.getImage()+"");
                                    intent.putExtra("shopAddress",item.getAddress());
                                    intent.putExtra("ratings", item.getRating());
                                    intent.putExtra("res_id", item.getId()+"");
                                    intent.putExtra("dTime", item.getDeliveryTime());
                                    intent.putExtra("discount", item.getDiscount());
                                    intent.putExtra("discountMin", item.getDiscountMin());
                                    intent.putExtra("description", item.getDescription());
                                    intent.putExtra("phoneOne", item.getPhone());
                                    intent.putExtra("phoneTwo", item.getMobile());
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                }
                                else {
                                    StyleableToast.makeText(ShopHomeActivity.this, "This Restaurant not taking any order!", Toast.LENGTH_LONG, R.style.mytoast).show();

                                }

                            }
                        });
                    shopRv.setAdapter(shopAdapter);
                    progressDialog.setVisibility(View.GONE);
                }catch (Exception e){
                    progressDialog.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {

                progressDialog.setVisibility(View.GONE);
            }
        });
    }

    private void loadShopHighToLowData() {
        Call<ProductResponse> call = APIClient.getInstance().getOpenProductsByCostHighToLow(latitude,longitude);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                try {
                    ProductResponse body = response.body();
                    productFoods.clear();
                    productFoods= body.getProductFoodList();
                    if (body.getError_code()==0 && productFoods.size() > 0)
                        shopAdapter = new MyShopAdapter(ShopHomeActivity.this,productFoods, new MyShopAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(ProductFood item) {

                                if (item.getIsopen().equalsIgnoreCase("1")){
                                    Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                    intent.putExtra("shopName",item.getName());
                                    intent.putExtra("shopAvatar",item.getImage()+"");
                                    intent.putExtra("shopAddress",item.getAddress());
                                    intent.putExtra("ratings", item.getRating());
                                    intent.putExtra("res_id", item.getId()+"");
                                    intent.putExtra("dTime", item.getDeliveryTime());
                                    intent.putExtra("discount", item.getDiscount());
                                    intent.putExtra("discountMin", item.getDiscountMin());
                                    intent.putExtra("description", item.getDescription());
                                    intent.putExtra("phoneOne", item.getPhone());
                                    intent.putExtra("phoneTwo", item.getMobile());
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                }
                                else {
                                    StyleableToast.makeText(ShopHomeActivity.this, "This Restaurant not taking any order!", Toast.LENGTH_LONG, R.style.mytoast).show();

                                }

                            }
                        });
                    shopRv.setAdapter(shopAdapter);
                    progressDialog.setVisibility(View.GONE);
                }catch (Exception e){
                    progressDialog.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressDialog.setVisibility(View.GONE);
            }
        });


    }

    private void loadShopData() {
        Call<ProductResponse> call = APIClient.getInstance().getProductData(latitude, longitude);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                try{
                    ProductResponse body = response.body();
                    productFoods.clear();
                    productFoods= body.getProductFoodList();
                    if (body.getError_code()==0 && productFoods.size() > 0){
                        shopAdapter = new MyShopAdapter(ShopHomeActivity.this, productFoods, new MyShopAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(ProductFood item) {

                                if (item.getIsopen().equalsIgnoreCase("1")){
                                    Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                    intent.putExtra("shopName",item.getName());
                                    intent.putExtra("shopAvatar",item.getImage()+"");
                                    intent.putExtra("shopAddress",item.getAddress());
                                    intent.putExtra("ratings", item.getRating());
                                    intent.putExtra("res_id", item.getId()+"");
                                    intent.putExtra("dTime", item.getDeliveryTime());
                                    intent.putExtra("discount", item.getDiscount());
                                    intent.putExtra("discountMin", item.getDiscountMin());
                                    intent.putExtra("description", item.getDescription());
                                    intent.putExtra("phoneOne", item.getPhone());
                                    intent.putExtra("phoneTwo", item.getMobile());
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                }
                                else {
                                    StyleableToast.makeText(ShopHomeActivity.this, "This Restaurant not taking any order!", Toast.LENGTH_LONG, R.style.mytoast).show();

                                }

                            }
                        });
                        shopRv.setAdapter(shopAdapter);
                        progressDialog.setVisibility(View.GONE);
                    }

                }catch (Exception e){
                    progressDialog.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressDialog.setVisibility(View.GONE);
            }
        });



    }






    private void getRestaurantByCategory(String id) {

        Call<ProductResponse> call = APIClient.getInstance().getOpenProductsByCategory(id);

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                productFoods.clear();
                ProductResponse body = response.body();
                productFoods= body.getProductFoodList();

                if (body.getError_code()==0 && productFoods.size() > 0)
                    shopAdapter = new MyShopAdapter(ShopHomeActivity.this,productFoods, new MyShopAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(ProductFood item) {

                            if (item.getIsopen().equalsIgnoreCase("1")){
                                Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                intent.putExtra("shopName",item.getName());
                                intent.putExtra("shopAvatar",item.getImage()+"");
                                intent.putExtra("shopAddress",item.getAddress());
                                intent.putExtra("ratings", item.getRating());
                                intent.putExtra("res_id", item.getId()+"");
                                intent.putExtra("dTime", item.getDeliveryTime());
                                intent.putExtra("discount", item.getDiscount());
                                intent.putExtra("discountMin", item.getDiscountMin());
                                intent.putExtra("description", item.getDescription());
                                intent.putExtra("phoneOne", item.getPhone());
                                intent.putExtra("phoneTwo", item.getMobile());
                                startActivity(intent);
                                overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                            }
                            else {
                                StyleableToast.makeText(ShopHomeActivity.this, "This Restaurant not taking any order!", Toast.LENGTH_LONG, R.style.mytoast).show();

                            }

                        }
                    });
                shopRv.setAdapter(shopAdapter);
                progressDialog.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                progressDialog.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}