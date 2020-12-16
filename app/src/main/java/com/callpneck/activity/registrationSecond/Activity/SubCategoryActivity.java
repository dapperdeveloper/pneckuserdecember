package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.activity.registrationSecond.Adapter.MySubCategoryAdapter;
import com.callpneck.activity.registrationSecond.Model.SubCategory;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryActivity extends AppCompatActivity {

    TextView categoryTv;
    RecyclerView subCategoryRecyclerView;
    MySubCategoryAdapter adapter;
    List<SubCategory> categoryList;
    String categoryName;

    Button nextBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_sub_category);

        subCategoryRecyclerView = findViewById(R.id.subCategoryRecyclerView);
        categoryTv = findViewById(R.id.categoryTv);
        nextBtn = findViewById(R.id.nextBtn);

        Intent intent = getIntent();
        if (intent != null){
            categoryName = intent.getStringExtra("categoryName");
            categoryTv.setText(categoryName);
        }

        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadCategory();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubCategoryActivity.this, ProviderActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
            }
        });
    }

    private void loadCategory() {
        categoryList = new ArrayList<>();
        categoryList.add(new SubCategory(R.drawable.ic_taxi,"Taxi Service"));
        categoryList.add(new SubCategory(R.drawable.ic_taxi,"Taxi Service"));
        adapter = new MySubCategoryAdapter(this,categoryList);
        subCategoryRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }

}