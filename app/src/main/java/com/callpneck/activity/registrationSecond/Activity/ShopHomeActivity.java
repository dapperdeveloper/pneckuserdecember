package com.callpneck.activity.registrationSecond.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.Database.MainData;
import com.callpneck.activity.Database.RoomDB;
import com.callpneck.activity.registrationSecond.Adapter.MyRestaurantListAdapter;
import com.callpneck.activity.registrationSecond.Adapter.MyShopAdapter;
import com.callpneck.activity.registrationSecond.Model.FoodShop;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.Cuisines;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ProductResponse.ProductFood;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ProductResponse.ProductResponse;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ResponseFoodHome;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;
import com.callpneck.utils.Constants;
import com.google.android.material.snackbar.Snackbar;
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

    RecyclerView restaurantListRecyclerView;
    List<Cuisines> categoryList;
    MyRestaurantListAdapter adapter;
    TextView titleTv, filterTv;
    String title;

    private EditText searchView;
    RecyclerView shopRv;
    List<FoodShop> shopList;
    List<ProductFood> productFoods;
    MyShopAdapter shopAdapter;

    ResponseFoodHome responseFoodHome;


    private AVLoadingIndicatorView  progressDialog;
    LinearLayout cuisinesLayout;

    private SessionManager sessionManager;

    RoomDB database;
    List<MainData> dataList = new ArrayList<>();

    String latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_food_home);

        Intent intent = getIntent();
        if (intent != null){
            categoryName = intent.getStringExtra("categoryName");
        }

        restaurantListRecyclerView = findViewById(R.id.restaurantListRecyclerView);
        titleTv = findViewById(R.id.titleTv);
        shopRv = findViewById(R.id.shopRv);
        filterTv = findViewById(R.id.filterTv);
        searchView = findViewById(R.id.searchView);
        cuisinesLayout = findViewById(R.id.cuisinesLayout);

        progressDialog = findViewById(R.id.progress_bar);
        categoryList = new ArrayList<>();
        shopList = new ArrayList<>();
        productFoods = new ArrayList<>();

        initView();

        sessionManager = new SessionManager(this);
        latitude = sessionManager.getUserLatitude();
        longitude = sessionManager.getUserLongitude();

        if (AppController.isConnected(ShopHomeActivity.this)){
            loadCategoryData();
            if (validation())
                loadShopData();
            search();
        }


        //Initialize database
        database = RoomDB.getInstance(this);
        //store database value in data list
        dataList = database.mainDao().getAll();
        if (dataList != null)
            deleteItemFromCart();

        titleTv.setText("All");


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

    private boolean validation(){
        boolean valid = true;
        if(TextUtils.isEmpty(latitude)){
            showSnackBar(ShopHomeActivity.this, "Location not set yet!");
            valid = false;
        }
        else if(TextUtils.isEmpty(longitude)){
           showSnackBar(ShopHomeActivity.this, "Location not set yet!");
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
                   showSnackBar(ShopHomeActivity.this,"No Result Found...!");
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

                                Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                intent.putExtra("shopName",item.getName());
                                intent.putExtra("shopAvatar",item.getImage()+"");
                                intent.putExtra("shopAddress",item.getAddress());
                                intent.putExtra("ratings", item.getRating());
                                intent.putExtra("res_id", item.getId()+"");
                                intent.putExtra("dTime", item.getDeliveryTime());
                                intent.putExtra("discount", item.getDiscount());
                                intent.putExtra("discountMin", item.getDiscountMin());
                                startActivity(intent);
                                overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);

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

                                Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                intent.putExtra("shopName",item.getName());
                                intent.putExtra("shopAvatar",item.getImage()+"");
                                intent.putExtra("shopAddress",item.getAddress());
                                intent.putExtra("res_id", item.getId()+"");
                                intent.putExtra("ratings", item.getRating());
                                intent.putExtra("dTime", item.getDeliveryTime());
                                intent.putExtra("discount", item.getDiscount());
                                intent.putExtra("discountMin", item.getDiscountMin());
                                startActivity(intent);
                                overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);

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

                                Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                intent.putExtra("shopName",item.getName());
                                intent.putExtra("shopAvatar",item.getImage()+"");
                                intent.putExtra("shopAddress",item.getAddress());
                                intent.putExtra("res_id", item.getId()+"");
                                intent.putExtra("ratings", item.getRating());
                                intent.putExtra("dTime", item.getDeliveryTime());
                                intent.putExtra("discount", item.getDiscount());
                                intent.putExtra("discountMin", item.getDiscountMin());
                                startActivity(intent);
                                overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);

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

                                Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                intent.putExtra("shopName",item.getName());
                                intent.putExtra("shopAvatar",item.getImage()+"");
                                intent.putExtra("shopAddress",item.getAddress());
                                intent.putExtra("ratings", item.getRating());
                                intent.putExtra("res_id", item.getId()+"");
                                intent.putExtra("dTime", item.getDeliveryTime());
                                intent.putExtra("discount", item.getDiscount());
                                intent.putExtra("discountMin", item.getDiscountMin());
                                startActivity(intent);
                                overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);

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

                                Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                intent.putExtra("shopName",item.getName());
                                intent.putExtra("shopAvatar",item.getImage()+"");
                                intent.putExtra("ratings", item.getRating());
                                intent.putExtra("shopAddress",item.getAddress());
                                intent.putExtra("res_id", item.getId()+"");
                                intent.putExtra("dTime", item.getDeliveryTime());
                                intent.putExtra("discount", item.getDiscount());
                                intent.putExtra("discountMin", item.getDiscountMin());
                                startActivity(intent);
                                overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);

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

                                Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                intent.putExtra("shopName",item.getName());
                                intent.putExtra("shopAvatar",item.getImage()+"");
                                intent.putExtra("ratings", item.getRating());
                                intent.putExtra("shopAddress",item.getAddress());
                                intent.putExtra("res_id", item.getId()+"");
                                intent.putExtra("dTime", item.getDeliveryTime());
                                intent.putExtra("discount", item.getDiscount());
                                intent.putExtra("discountMin", item.getDiscountMin());
                                startActivity(intent);
                                overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);

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

                                Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                intent.putExtra("shopName",item.getName());
                                intent.putExtra("shopAvatar",item.getImage()+"");
                                intent.putExtra("shopAddress",item.getAddress());
                                intent.putExtra("ratings", item.getRating());
                                intent.putExtra("res_id", item.getId()+"");
                                intent.putExtra("dTime", item.getDeliveryTime());
                                intent.putExtra("discount", item.getDiscount());
                                intent.putExtra("discountMin", item.getDiscountMin());
                                startActivity(intent);
                                overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);

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

                                Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                                intent.putExtra("shopName",item.getName());
                                intent.putExtra("shopAvatar",item.getImage()+"");
                                intent.putExtra("shopAddress",item.getAddress());
                                intent.putExtra("ratings", item.getRating());
                                intent.putExtra("res_id", item.getId()+"");
                                intent.putExtra("dTime", item.getDeliveryTime());
                                intent.putExtra("discount", item.getDiscount());
                                intent.putExtra("discountMin", item.getDiscountMin());
                                startActivity(intent);
                                overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);

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


    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        restaurantListRecyclerView.setLayoutManager(layoutManager);
    }

    public static void showSnackBar(Activity activity, String snackTitle) {
        View Parentview=activity.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(Parentview, snackTitle, Snackbar.LENGTH_SHORT);
        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    private void loadCategoryData() {
        progressDialog.setVisibility(View.VISIBLE);
        Call<ResponseFoodHome> call = APIClient.getInstance().getCategoryData();
        call.enqueue(new Callback<ResponseFoodHome>() {
            @Override
            public void onResponse(Call<ResponseFoodHome> call, Response<ResponseFoodHome> response) {

                responseFoodHome = response.body();
                if (responseFoodHome.getError_code()==0 &&responseFoodHome.getCuisines().size()>0)
                    adapter = new MyRestaurantListAdapter(ShopHomeActivity.this, responseFoodHome.getCuisines(), new MyRestaurantListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Cuisines item) {
//                       getRestaurantByCategory(item.getId()+"");
                            titleTv.setText(item.getName());

                        }
                    });
                restaurantListRecyclerView.setAdapter(adapter);
                progressDialog.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseFoodHome> call, Throwable t) {
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

                            Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                            intent.putExtra("shopName",item.getName());
                            intent.putExtra("shopAvatar",item.getImage()+"");
                            intent.putExtra("shopAddress",item.getAddress());
                            intent.putExtra("ratings", item.getRating());
                            intent.putExtra("res_id", item.getId()+"");
                            intent.putExtra("dTime", item.getDeliveryTime());
                            intent.putExtra("discount", item.getDiscount());
                            intent.putExtra("discountMin", item.getDiscountMin());
                            startActivity(intent);
                            overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);

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