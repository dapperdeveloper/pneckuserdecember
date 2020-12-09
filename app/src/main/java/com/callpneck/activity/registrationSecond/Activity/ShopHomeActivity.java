package com.callpneck.activity.registrationSecond.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.Database.MainData;
import com.callpneck.activity.Database.RoomDB;
import com.callpneck.activity.registrationSecond.Adapter.MyRestaurantListAdapter;
import com.callpneck.activity.registrationSecond.Adapter.MyShopAdapter;
import com.callpneck.activity.registrationSecond.Model.FoodShop;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.Cuisines;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ProductResponse.ProductFood;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ProductResponse.ProductResponse;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ResponseFoodHome;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.productListResponse.ShopDataList;
import com.callpneck.activity.registrationSecond.api.ApiClient;
import com.callpneck.activity.registrationSecond.api.ApiInterface;
import com.callpneck.utils.Constants;
import com.callpneck.utils.InternetConnection;
import com.nex3z.notificationbadge.NotificationBadge;
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

        Toast.makeText(this, ""+latitude+"\n"+longitude, Toast.LENGTH_SHORT).show();
        if (InternetConnection.checkConnection(ShopHomeActivity.this)){
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
                                    if (InternetConnection.checkConnection(ShopHomeActivity.this))
                                    loadShopData();
                                    titleTv.setText(selected);
                                }
                                if(selected.equals("High To Low")){
                                    if (InternetConnection.checkConnection(ShopHomeActivity.this))
                                    loadShopHighToLowData();
                                    titleTv.setText(selected);
                                }
                                if(selected.equals("Offer")){
                                    if (InternetConnection.checkConnection(ShopHomeActivity.this))
                                    loadShopOfferData();
                                    titleTv.setText(selected);
                                }
                                if(selected.equals("Low To High")){
                                    if (InternetConnection.checkConnection(ShopHomeActivity.this))
                                    loadShopLowToHighData();
                                    titleTv.setText(selected);
                                }
                                if(selected.equals("Delivery Time")){
                                    if (InternetConnection.checkConnection(ShopHomeActivity.this))
                                    loadShopByDeliveryTime();
                                    titleTv.setText(selected);
                                }
                                if(selected.equals("Ratings")){
                                    if (InternetConnection.checkConnection(ShopHomeActivity.this))
                                    loadShopByRatings();
                                    titleTv.setText(selected);
                                }
                                if(selected.equals("Relevance")){
                                    if (InternetConnection.checkConnection(ShopHomeActivity.this))
                                    loadShopByRelevance();
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
            Toast.makeText(this, "Latitude is required", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(longitude)){
            Toast.makeText(this, "Longitude is required", Toast.LENGTH_SHORT).show();
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
                    getProductDetail(editable.toString());
                    cuisinesLayout.setVisibility(View.GONE);
                }

                else {

                    cuisinesLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(ShopHomeActivity.this, "No Result Found...!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getProductDetail(String name) {

        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        RequestBody requestResId  = RequestBody.create(MediaType.parse("multipart/form-data"), name);
        Call<ProductResponse> call = apiInterface.restaurantSearchByName(requestResId);

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
                            intent.putExtra("shopAddress",item.getAddress());
                            intent.putExtra("ratings", item.getRating());
                            intent.putExtra("res_id", item.getId()+"");
                            intent.putExtra("dTime", item.getDeliveryTime());
                            intent.putExtra("discount", item.getDiscount());
                            intent.putExtra("discountMin", item.getDiscountMin());
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

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
    protected void onResume() {
        super.onResume();
    }


    private void loadShopByRelevance() {
        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Call<ProductResponse> call = apiInterface.getOpenProductsByRelevance(latitude, longitude);
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
                            intent.putExtra("shopAddress",item.getAddress());
                            intent.putExtra("res_id", item.getId()+"");
                            intent.putExtra("ratings", item.getRating());
                            intent.putExtra("dTime", item.getDeliveryTime());
                            intent.putExtra("discount", item.getDiscount());
                            intent.putExtra("discountMin", item.getDiscountMin());
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

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

    private void loadShopByRatings() {
        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Call<ProductResponse> call = apiInterface.getOpenProductsByrRating(latitude, longitude);
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
                            intent.putExtra("shopAddress",item.getAddress());
                            intent.putExtra("res_id", item.getId()+"");
                            intent.putExtra("ratings", item.getRating());
                            intent.putExtra("dTime", item.getDeliveryTime());
                            intent.putExtra("discount", item.getDiscount());
                            intent.putExtra("discountMin", item.getDiscountMin());
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

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

    private void loadShopByDeliveryTime() {

        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Call<ProductResponse> call = apiInterface.getOpenProductsByDeliveryTime(latitude, longitude);
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
                            intent.putExtra("shopAddress",item.getAddress());
                            intent.putExtra("ratings", item.getRating());
                            intent.putExtra("res_id", item.getId()+"");
                            intent.putExtra("dTime", item.getDeliveryTime());
                            intent.putExtra("discount", item.getDiscount());
                            intent.putExtra("discountMin", item.getDiscountMin());
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

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

    private void loadShopLowToHighData() {
        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Call<ProductResponse> call = apiInterface.getOpenProductsByCostLowToHigh(latitude, longitude);
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
                            intent.putExtra("ratings", item.getRating());
                            intent.putExtra("shopAddress",item.getAddress());
                            intent.putExtra("res_id", item.getId()+"");
                            intent.putExtra("dTime", item.getDeliveryTime());
                            intent.putExtra("discount", item.getDiscount());
                            intent.putExtra("discountMin", item.getDiscountMin());
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

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

    private void loadShopOfferData() {
        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Call<ProductResponse> call = apiInterface.getOpenProductsByOffer(latitude, longitude);
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
                            intent.putExtra("ratings", item.getRating());
                            intent.putExtra("shopAddress",item.getAddress());
                            intent.putExtra("res_id", item.getId()+"");
                            intent.putExtra("dTime", item.getDeliveryTime());
                            intent.putExtra("discount", item.getDiscount());
                            intent.putExtra("discountMin", item.getDiscountMin());
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

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

    private void loadShopHighToLowData() {
        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Call<ProductResponse> call = apiInterface.getOpenProductsByCostHighToLow(latitude,longitude);
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
                            intent.putExtra("shopAddress",item.getAddress());
                            intent.putExtra("ratings", item.getRating());
                            intent.putExtra("res_id", item.getId()+"");
                            intent.putExtra("dTime", item.getDeliveryTime());
                            intent.putExtra("discount", item.getDiscount());
                            intent.putExtra("discountMin", item.getDiscountMin());
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

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

    private void loadShopData() {

        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Call<ProductResponse> call = apiInterface.getProductData(latitude, longitude);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                productFoods.clear();
                ProductResponse body = response.body();
                productFoods= body.getProductFoodList();
                if (body.getError_code()==0 && productFoods.size() > 0)
                    shopAdapter = new MyShopAdapter(ShopHomeActivity.this, productFoods, new MyShopAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(ProductFood item) {

                            Intent intent = new Intent(ShopHomeActivity.this, ShopDetailActivity.class);
                            intent.putExtra("shopName",item.getName());
                            intent.putExtra("shopAddress",item.getAddress());
                            intent.putExtra("ratings", item.getRating());
                            intent.putExtra("res_id", item.getId()+"");
                            intent.putExtra("dTime", item.getDeliveryTime());
                            intent.putExtra("discount", item.getDiscount());
                            intent.putExtra("discountMin", item.getDiscountMin());
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

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


    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        restaurantListRecyclerView.setLayoutManager(layoutManager);
    }



    private void loadCategoryData() {
        progressDialog.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Call<ResponseFoodHome> call = apiInterface.getCategoryData();
        call.enqueue(new Callback<ResponseFoodHome>() {
            @Override
            public void onResponse(Call<ResponseFoodHome> call, Response<ResponseFoodHome> response) {

                responseFoodHome = response.body();
                if (responseFoodHome.getError_code()==0 &&responseFoodHome.getCuisines().size()>0)
               adapter = new MyRestaurantListAdapter(ShopHomeActivity.this, responseFoodHome.getCuisines(), new MyRestaurantListAdapter.OnItemClickListener() {
                   @Override
                   public void onItemClick(Cuisines item) {
                       getRestaurantByCategory(item.getId()+"");
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

        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Call<ProductResponse> call = apiInterface.getOpenProductsByCategory(id);

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
                            intent.putExtra("shopAddress",item.getAddress());
                            intent.putExtra("ratings", item.getRating());
                            intent.putExtra("res_id", item.getId()+"");
                            intent.putExtra("dTime", item.getDeliveryTime());
                            intent.putExtra("discount", item.getDiscount());
                            intent.putExtra("discountMin", item.getDiscountMin());
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

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

    private void openShortByCategoryActivity() {
        Intent intent = new Intent(ShopHomeActivity.this, ShortByCategoryRestuarantActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }


    private void openEditCartActivity() {

        Intent intent = new Intent(ShopHomeActivity.this, EditCartActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

    }
}