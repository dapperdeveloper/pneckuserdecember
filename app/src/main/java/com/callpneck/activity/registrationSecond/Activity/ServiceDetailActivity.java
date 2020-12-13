package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.activity.AppController;
import com.callpneck.activity.registrationSecond.Adapter.AdapterReview;
import com.callpneck.activity.registrationSecond.Adapter.ImageAdapter;
import com.callpneck.activity.registrationSecond.Adapter.MyServicesAdapter;
import com.callpneck.activity.registrationSecond.Adapter.ShopProductAdapter;
import com.callpneck.activity.registrationSecond.Model.Banner;
import com.callpneck.activity.registrationSecond.Model.GalleryResponse.ServiceGalleyResponse;
import com.callpneck.activity.registrationSecond.Model.ModelReview;
import com.callpneck.activity.registrationSecond.Model.ModelServices;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.productListResponse.ShopDataList;
import com.callpneck.activity.registrationSecond.api.ApiClient;
import com.callpneck.activity.registrationSecond.api.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailActivity extends AppCompatActivity {

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

    //ProductList
    List<Banner> productList;
    ShopProductAdapter shopProductAdapter;


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

        //loadServices();

        loadProductList();

        loadReviews();



    }

    private void loadProductList() {
        productList = new ArrayList<>();
        productList.add(new Banner(R.drawable.abcdp,"New Inspiron 14 5402 Laptop", "inStock"));
        productList.add(new Banner(R.drawable.banner,"Mi Notebook 14 Horizon Gray","OutOfStock"));
        productList.add(new Banner(R.drawable.abcdp,"New Inspiron 14 5402 Laptop","inStock"));
        shopProductAdapter = new ShopProductAdapter(this,productList);
        serviceRv.setAdapter(shopProductAdapter);


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

    private void loadServices() {
        servicesList.add(new ModelServices("Service #1","Rs.560.00"));
        servicesList.add(new ModelServices("Service #2","Rs.450.00"));
        servicesList.add(new ModelServices("Service #3","Rs.390.00"));
        servicesList.add(new ModelServices("Service #4","Rs.300.00"));
        adapter = new MyServicesAdapter(this,servicesList);
        serviceRv.setAdapter(adapter);
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
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

    }


}