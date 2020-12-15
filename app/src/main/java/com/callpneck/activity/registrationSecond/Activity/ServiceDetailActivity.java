package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.Database.MainData;
import com.callpneck.activity.Database.RoomDB;

import com.callpneck.activity.registrationSecond.Adapter.AdapterReview;
import com.callpneck.activity.registrationSecond.Adapter.ImageAdapter;
import com.callpneck.activity.registrationSecond.Adapter.MyServicesAdapter;
import com.callpneck.activity.registrationSecond.Adapter.ShopProductAdapter;
import com.callpneck.activity.registrationSecond.Model.GalleryResponse.ServiceGalleyResponse;
import com.callpneck.activity.registrationSecond.Model.ModelReview;
import com.callpneck.activity.registrationSecond.Model.ModelServices;
import com.callpneck.activity.registrationSecond.Model.Product;
import com.callpneck.activity.registrationSecond.Model.ProductModel;
import com.callpneck.activity.registrationSecond.api.ApiClient;
import com.callpneck.activity.registrationSecond.api.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ServiceDetailActivity extends AppCompatActivity {

    private TextView cartCountTv;
    ImageButton cartBtn;
    TextView tabServiceTv, tabGalleryTv, tabReviewsTv, shopNameTv, nameTitle;
    RelativeLayout serviceRl, galleryRl, reviewRl;
    RecyclerView serviceRv,  reviewRv;
    GridView grid_view;
    CircleImageView shopAvatarIv;
    List<ModelServices> servicesList;
    MyServicesAdapter adapter;
    ArrayList<ModelReview> reviewList;
    AdapterReview adapterReview;
    RatingBar rating_bar;
    String shopId, shopName, shopAvatar, shopRating, shopDescription;
    List<String> galleryList;
    ImageAdapter imageAdapter;
    private SessionManager sessionManager;
    //ProductList
    private List<Product> productList;
    ShopProductAdapter shopProductAdapter;
    List<Product> mainPorudctList = new ArrayList<>();
    private ProgressDialog progressDialog;
    RoomDB database;
    List<MainData> dataList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_service_detail);
        shopNameTv = findViewById(R.id.shopNameTv);
        shopAvatarIv = findViewById(R.id.shopAvatarIv);
        rating_bar = findViewById(R.id.rating_bar);
        tabServiceTv = findViewById(R.id.tabServiceTv);
        tabGalleryTv = findViewById(R.id.tabGalleryTv);
        tabReviewsTv = findViewById(R.id.tabReviewsTv);
        serviceRl = findViewById(R.id.serviceRl);
        galleryRl = findViewById(R.id.galleryRl);
        reviewRl = findViewById(R.id.reviewRl);
        serviceRv = findViewById(R.id.serviceRv);
        reviewRv = findViewById(R.id.reviewRv);
        nameTitle = findViewById(R.id.nameTitle);
        grid_view = findViewById(R.id.grid_view);
        cartCountTv = findViewById(R.id.cartCountTv);
        cartBtn = findViewById(R.id.cartBtn);

        if (getIntent() != null){
            shopId = getIntent().getStringExtra("shopId");
            shopName = getIntent().getStringExtra("shopName");
            shopAvatar = getIntent().getStringExtra("shopAvatar");
            shopRating = getIntent().getStringExtra("shopRating");
            try {
                float rating = Float.parseFloat(shopRating);
                rating_bar.setRating(rating);
            }catch (Exception e){
                rating_bar.setRating(1.5f);
            }


            Glide.with(this).load(shopAvatar).placeholder(R.drawable.ic_user_replace).into(shopAvatarIv);
            shopNameTv.setText(shopName);
            nameTitle.setText(shopName+" Details");
        }
        sessionManager=new SessionManager(ServiceDetailActivity.this);
        database = RoomDB.getInstance(this);

        productList = new ArrayList<>();
        serviceRv.setHasFixedSize(true);
        shopProductAdapter= new ShopProductAdapter(this,productList);
        serviceRv.setAdapter(shopProductAdapter);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        shopDescription = "Buy cosmetics & beauty products online from Nykaa, the online shopping beauty store. Browse makeup, health products & more from top beauty brands.";

        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.viewProfileTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openServiceDescriptionActivity();
            }
        });
        cartCount();
        servicesList = new ArrayList<>();
        reviewList = new ArrayList<>();
        showServicesUI();
        tabServiceTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               showServicesUI();
            }
        });
        tabGalleryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showGalleryUI();
                if (AppController.isConnected(ServiceDetailActivity.this))
                getGalleryImage();
            }
        });
        tabReviewsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showReviewUI();
            }
        });

        getProduct();
        loadReviews();



    }
    public void cartCount() {
        //store database value in data list
        dataList = database.mainDao().getAll();
        int count = dataList.size();
        if(count<=0){
            cartCountTv.setVisibility(View.GONE);
        }else {
            Toast.makeText(this, ""+count, Toast.LENGTH_SHORT).show();
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

    private void getProduct() {
        progressDialog.setMessage("adding");
        progressDialog.show();
        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Call<ProductModel> call = apiInterface.getProduct(shopId);
        call.enqueue(new Callback<ProductModel>() {
            @Override
            public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
                try {
                    ProductModel model = response.body();
                    progressDialog.dismiss();
                    productList.addAll(model.getProductList());
                    shopProductAdapter.notifyDataSetChanged();
                }catch (Exception e){

                }

            }
            @Override
            public void onFailure(Call<ProductModel> call, Throwable t) {
                Toast.makeText(ServiceDetailActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    private void getGalleryImage() {
        galleryList = new ArrayList<>();
        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Call<ServiceGalleyResponse> call = apiInterface.getGallery(shopId);
        call.enqueue(new Callback<ServiceGalleyResponse>() {
            @Override
            public void onResponse(Call<ServiceGalleyResponse> call, Response<ServiceGalleyResponse> response) {

               try {
                   ServiceGalleyResponse model = response.body();
                   if (model != null && model.getResponse().getSuccess()){
                       galleryList.clear();
                       galleryList = model.getResponse().getData().getImage();
                       imageAdapter = new ImageAdapter(ServiceDetailActivity.this,galleryList);
                       grid_view.setAdapter(imageAdapter);
                       grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                           @Override
                           public void onItemClick(AdapterView<?> parent, View view,
                                                   int position, long id) {

                               Intent fullScreenIntent = new Intent(ServiceDetailActivity.this, FullScreenImageActivity.class);
                               fullScreenIntent.setData(Uri.parse(galleryList.get(position)));
                               startActivity(fullScreenIntent);
                           }
                       });
                   }
               }catch (Exception e){
                   e.printStackTrace();
               }

            }

            @Override
            public void onFailure(Call<ServiceGalleyResponse> call, Throwable t) {
                Toast.makeText(ServiceDetailActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openServiceDescriptionActivity() {
        Intent intent = new Intent(ServiceDetailActivity.this, ServiceDetailDescriptionActivity.class);
        intent.putExtra("shopDescription", shopDescription);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    private void loadReviews() {
        reviewList.add(new ModelReview("Jimmy"));
        reviewList.add(new ModelReview("Chu Chu"));
        reviewList.add(new ModelReview("Ramesh"));
        adapterReview = new AdapterReview(this, reviewList);
        reviewRv.setAdapter(adapterReview);
    }

    private void showServicesUI() {
        serviceRl.setVisibility(View.VISIBLE);
        galleryRl.setVisibility(View.GONE);
        reviewRl.setVisibility(View.GONE);
        tabServiceTv.setTextColor(getResources().getColor(R.color.white));
        tabServiceTv.setBackgroundResource(R.drawable.shape_rect04);

        tabGalleryTv.setTextColor(getResources().getColor(R.color.black));
        tabGalleryTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabReviewsTv.setTextColor(getResources().getColor(R.color.black));
        tabReviewsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
    private void showGalleryUI() {

        serviceRl.setVisibility(View.GONE);
        galleryRl.setVisibility(View.VISIBLE);
        reviewRl.setVisibility(View.GONE);
        tabGalleryTv.setTextColor(getResources().getColor(R.color.white));
        tabGalleryTv.setBackgroundResource(R.drawable.shape_rect04);

        tabServiceTv.setTextColor(getResources().getColor(R.color.black));
        tabServiceTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabReviewsTv.setTextColor(getResources().getColor(R.color.black));
        tabReviewsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showReviewUI() {

        serviceRl.setVisibility(View.GONE);
        galleryRl.setVisibility(View.GONE);
        reviewRl.setVisibility(View.VISIBLE);
        tabReviewsTv.setTextColor(getResources().getColor(R.color.white));
        tabReviewsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabGalleryTv.setTextColor(getResources().getColor(R.color.black));
        tabGalleryTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabServiceTv.setTextColor(getResources().getColor(R.color.black));
        tabServiceTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
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
            overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
        }
        }

    //    @Override
//    public void onBackPressed() {
//
//        finish();
//        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
//
//    }


}