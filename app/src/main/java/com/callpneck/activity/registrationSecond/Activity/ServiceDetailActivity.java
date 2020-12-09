package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.activity.registrationSecond.Adapter.AdapterReview;
import com.callpneck.activity.registrationSecond.Adapter.MyServicesAdapter;
import com.callpneck.activity.registrationSecond.Model.ModelReview;
import com.callpneck.activity.registrationSecond.Model.ModelServices;

import java.util.ArrayList;
import java.util.List;

public class ServiceDetailActivity extends AppCompatActivity {

    TextView tabServiceTv, tabGalleryTv, tabReviewsTv;
    RelativeLayout serviceRl, galleryRl, reviewRl;
    RecyclerView serviceRv, galleryRv, reviewRv;

    List<ModelServices> servicesList;
    MyServicesAdapter adapter;
    ArrayList<ModelReview> reviewList;
    AdapterReview adapterReview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_service_detail);

        tabServiceTv = findViewById(R.id.tabServiceTv);
        tabGalleryTv = findViewById(R.id.tabGalleryTv);
        tabReviewsTv = findViewById(R.id.tabReviewsTv);
        serviceRl = findViewById(R.id.serviceRl);
        galleryRl = findViewById(R.id.galleryRl);
        reviewRl = findViewById(R.id.reviewRl);
        serviceRv = findViewById(R.id.serviceRv);
        galleryRv = findViewById(R.id.galleryRv);
        reviewRv = findViewById(R.id.reviewRv);

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
            }
        });
        tabReviewsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showReviewUI();
            }
        });

        loadServices();

        loadReviews();


    }

    private void openServiceDescriptionActivity() {
        Intent intent = new Intent(ServiceDetailActivity.this, ServiceDetailDescriptionActivity.class);
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