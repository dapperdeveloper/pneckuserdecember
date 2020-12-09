package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;

public class NotificationsActivity extends AppCompatActivity {

    private TextView tabAllTv,tabNotificationsTv, tabNewsTv;
    private RecyclerView allRv, notificationsRv, newsRv;
    private RelativeLayout allRl, notificationsRl, newsRl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_notifications);

        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        init();

        showAllUI();
        tabAllTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load all
                showAllUI();
            }
        });

        tabNotificationsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load notification
                showNotificationsUI();
            }
        });

        tabNewsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load news
                showNewsUI();
            }
        });

    }

    private void showNewsUI() {

        allRl.setVisibility(View.GONE);
        notificationsRl.setVisibility(View.GONE);
        newsRl.setVisibility(View.VISIBLE);
        tabNewsTv.setTextColor(getResources().getColor(R.color.white));
        tabNewsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabAllTv.setTextColor(getResources().getColor(R.color.black));
        tabAllTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabNotificationsTv.setTextColor(getResources().getColor(R.color.black));
        tabNotificationsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showNotificationsUI() {

        allRl.setVisibility(View.GONE);
        notificationsRl.setVisibility(View.VISIBLE);
        newsRl.setVisibility(View.GONE);
        tabNotificationsTv.setTextColor(getResources().getColor(R.color.white));
        tabNotificationsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabAllTv.setTextColor(getResources().getColor(R.color.black));
        tabAllTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabNewsTv.setTextColor(getResources().getColor(R.color.black));
        tabNewsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showAllUI() {

        allRl.setVisibility(View.VISIBLE);
        notificationsRl.setVisibility(View.GONE);
        newsRl.setVisibility(View.GONE);
        tabAllTv.setTextColor(getResources().getColor(R.color.white));
        tabAllTv.setBackgroundResource(R.drawable.shape_rect04);

        tabNotificationsTv.setTextColor(getResources().getColor(R.color.black));
        tabNotificationsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabNewsTv.setTextColor(getResources().getColor(R.color.black));
        tabNewsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }
    private void init() {
        tabAllTv = findViewById(R.id.tabAllTv);
        tabNotificationsTv = findViewById(R.id.tabNotificationsTv);
        tabNewsTv = findViewById(R.id.tabNewsTv);


        allRl = findViewById(R.id.allRl);
        notificationsRl = findViewById(R.id.notificationsRl);
        newsRl = findViewById(R.id.newsRl);

        allRv = findViewById(R.id.allRv);
        notificationsRv = findViewById(R.id.notificationsRv);
        newsRv = findViewById(R.id.newsRv);

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

    }
}