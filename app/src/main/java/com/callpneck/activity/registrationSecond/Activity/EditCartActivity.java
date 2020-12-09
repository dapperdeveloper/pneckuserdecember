package com.callpneck.activity.registrationSecond.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.Database.MainData;
import com.callpneck.activity.Database.RoomDB;
import com.callpneck.activity.registrationSecond.Adapter.MyCartAdapter;

import java.util.ArrayList;
import java.util.List;

public class EditCartActivity extends AppCompatActivity {

    RecyclerView recycler_cart;
    public TextView txt_total_price, shopNameTv, shopAddressTv;
    private SessionManager sessionManager;
    public Button checkoutBtn;

    List<MainData> dataList = new ArrayList<>();

    RoomDB database;

    MyCartAdapter adapter;

    public double allTotalPrice =0.00;
    String res_id = "";
    String shopName, shopAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_edit_cart);
        recycler_cart = findViewById(R.id.cartRv);
        txt_total_price =findViewById(R.id.txt_total_price);
        checkoutBtn = findViewById(R.id.checkoutBtn);
        shopNameTv = findViewById(R.id.shopNameTv);
        shopAddressTv = findViewById(R.id.shopAddressTv);
        sessionManager = new SessionManager(this);

        if (getIntent() != null){
            res_id = getIntent().getStringExtra("res_id");
            shopName = getIntent().getStringExtra("shopName");
            shopAddress = getIntent().getStringExtra("shopAddress");
            shopNameTv.setText(shopName);
            shopAddressTv.setText(shopAddress);
        }
        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        recycler_cart.setHasFixedSize(true);
        //Initialize database
        database = RoomDB.getInstance(this);
        //store database value in data list
        dataList = database.mainDao().getAll();

        if (dataList.size() == 0){
            checkoutBtn.setVisibility(View.GONE);
        }else {
            checkoutBtn.setVisibility(View.VISIBLE);
        }

        for(int i =0;i<dataList.size();i++){
            String cost = dataList.get(i).getCost();
            allTotalPrice = allTotalPrice + Double.parseDouble(cost);
        }

        txt_total_price.setText("Rs." + allTotalPrice);

        //Initialize adapter
        adapter = new MyCartAdapter(dataList, EditCartActivity.this);

        recycler_cart.setAdapter(adapter);


        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openCheckoutActivity();
            }
        });



    }

    private void openCheckoutActivity() {
        Intent intent = new Intent(EditCartActivity.this, CheckoutActivity.class);
        intent.putExtra("res_id",res_id);
        intent.putExtra("total_amount",allTotalPrice+"");
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

    }

}