package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;

public class TransactionsActivity extends AppCompatActivity {

    private TextView tabAllTv,tabMoneyInTv, tabMoneyOutTv, noTransactionTv;
    private RecyclerView allRv, moneyInRv, moneyOutRv;
    private RelativeLayout allRl, moneyInRl, moneyOutRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_my_transactions);


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
        tabMoneyInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load moneyIn
                showInUI();
            }
        });
        tabMoneyOutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load moneyOut
                showOutUI();
            }
        });

    }

    private void showOutUI() {
        //show money out ui  and hide in/all ui
        allRl.setVisibility(View.GONE);
        moneyInRl.setVisibility(View.GONE);
        moneyOutRl.setVisibility(View.VISIBLE);
        tabMoneyOutTv.setTextColor(getResources().getColor(R.color.white));
        tabMoneyOutTv.setBackgroundResource(R.drawable.shape_rect04);

        tabAllTv.setTextColor(getResources().getColor(R.color.black));
        tabAllTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabMoneyInTv.setTextColor(getResources().getColor(R.color.black));
        tabMoneyInTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }


    private void showInUI() {

        //show money in ui  and hide all/out ui
        allRl.setVisibility(View.GONE);
        moneyInRl.setVisibility(View.VISIBLE);
        moneyOutRl.setVisibility(View.GONE);
        tabMoneyInTv.setTextColor(getResources().getColor(R.color.white));
        tabMoneyInTv.setBackgroundResource(R.drawable.shape_rect04);

        tabAllTv.setTextColor(getResources().getColor(R.color.black));
        tabAllTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabMoneyOutTv.setTextColor(getResources().getColor(R.color.black));
        tabMoneyOutTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    private void showAllUI() {

        //show all ui  and hide money in/out ui
        allRl.setVisibility(View.VISIBLE);
        moneyInRl.setVisibility(View.GONE);
        moneyOutRl.setVisibility(View.GONE);
        tabAllTv.setTextColor(getResources().getColor(R.color.white));
        tabAllTv.setBackgroundResource(R.drawable.shape_rect04);

        tabMoneyInTv.setTextColor(getResources().getColor(R.color.black));
        tabMoneyInTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabMoneyOutTv.setTextColor(getResources().getColor(R.color.black));
        tabMoneyOutTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    private void init() {
        tabAllTv = findViewById(R.id.tabAllTv);
        tabMoneyInTv = findViewById(R.id.tabMoneyInTv);
        tabMoneyOutTv = findViewById(R.id.tabMoneyOutTv);
        noTransactionTv = findViewById(R.id.noTransactionTv);
        allRl = findViewById(R.id.allRl);
        moneyInRl = findViewById(R.id.moneyInRl);
        moneyOutRl = findViewById(R.id.moneyOutRl);
        allRv = findViewById(R.id.allRv);
        moneyInRv = findViewById(R.id.moneyInRv);
        moneyOutRv = findViewById(R.id.moneyOutRv);

    }



    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

    }
}