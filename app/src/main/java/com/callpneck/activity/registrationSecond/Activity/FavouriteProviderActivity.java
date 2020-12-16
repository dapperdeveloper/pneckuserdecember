package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;

public class FavouriteProviderActivity extends AppCompatActivity {


    private TextView tabAllTv,tabFavoriteTv;
    private RecyclerView allRv, favouriteRv;
    private RelativeLayout allRl, favouriteRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_favourite_provider);

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
        tabFavoriteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load favorite
                showFavoriteUI();
            }
        });

    }

    private void init() {
        tabAllTv = findViewById(R.id.tabAllTv);
        tabFavoriteTv = findViewById(R.id.tabFavoriteTv);
        allRl = findViewById(R.id.allRl);
        favouriteRl = findViewById(R.id.favouriteRl);
        allRv = findViewById(R.id.allRv);
        favouriteRv = findViewById(R.id.favouriteRv);

    }
    private void showFavoriteUI() {

        allRl.setVisibility(View.GONE);
        favouriteRl.setVisibility(View.VISIBLE);
        tabFavoriteTv.setTextColor(getResources().getColor(R.color.white));
        tabFavoriteTv.setBackgroundResource(R.drawable.shape_rect04);

        tabAllTv.setTextColor(getResources().getColor(R.color.black));
        tabAllTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showAllUI() {

        allRl.setVisibility(View.VISIBLE);
        favouriteRl.setVisibility(View.GONE);
        tabAllTv.setTextColor(getResources().getColor(R.color.white));
        tabAllTv.setBackgroundResource(R.drawable.shape_rect04);

        tabFavoriteTv.setTextColor(getResources().getColor(R.color.black));
        tabFavoriteTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}