package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.Database.MainData;
import com.callpneck.activity.Database.RoomDB;
import com.callpneck.activity.registrationSecond.Adapter.MyCartAdapter;
import com.callpneck.activity.registrationSecond.Adapter.MyShopCartAdapter;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.helper.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditShopCartActivity extends AppCompatActivity {

    RecyclerView recycler_cart;
    public TextView txt_total_price;
    private SessionManager sessionManager;
    public Button checkoutBtn;

    List<MainData> dataList = new ArrayList<>();

    RoomDB database;

    MyShopCartAdapter adapter;

    public double allTotalPrice =0.00;
    String shop_id = "";
    String shopName, shopAddress;
    public LinearLayout lytempty, totalRL;
    Button btnShowNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_edit_shop_cart);
        recycler_cart = findViewById(R.id.cartRv);
        txt_total_price =findViewById(R.id.txt_total_price);
        checkoutBtn = findViewById(R.id.checkoutBtn);
        lytempty = findViewById(R.id.lytempty);
        totalRL = findViewById(R.id.totalRL);
        btnShowNow = findViewById(R.id.btnShowNow);
        sessionManager = new SessionManager(this);

        if (getIntent() != null){
            shop_id = getIntent().getStringExtra("res_id");
            shopName = getIntent().getStringExtra("shopName");
            shopAddress = getIntent().getStringExtra("shopAddress");
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
            totalRL.setVisibility(View.GONE);
            lytempty.setVisibility(View.VISIBLE);

        }else {
            totalRL.setVisibility(View.VISIBLE);
            lytempty.setVisibility(View.GONE);
        }

        for(int i =0;i<dataList.size();i++){
            String cost = dataList.get(i).getCost();
            allTotalPrice = allTotalPrice + Double.parseDouble(cost);
        }

        txt_total_price.setText("Rs." + allTotalPrice);

        //Initialize adapter
        adapter = new MyShopCartAdapter(dataList, EditShopCartActivity.this);

        recycler_cart.setAdapter(adapter);


        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openCheckoutActivity();
            }
        });
        btnShowNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });




        getDeliveryCharges();

    }
    private void getDeliveryCharges() {

        Call<JsonObject> call = APIClient.getInstance().deliveryCharge();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(new Gson().toJson(response.body()));

                    Constant.SETTING_DELIVERY_CHARGE = jsonObject.getDouble("data");

                }catch (Exception e){
                    e.toString();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    private void openCheckoutActivity() {
        Intent intent = new Intent(EditShopCartActivity.this, CheckoutShopActivity.class);
        intent.putExtra("res_id", shop_id);
        intent.putExtra("total_amount",allTotalPrice+"");
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}