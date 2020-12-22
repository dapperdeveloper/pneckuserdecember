package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Adapter.MoneyInAdapter;
import com.callpneck.activity.registrationSecond.Adapter.MoneyOutAdapter;
import com.callpneck.activity.registrationSecond.Adapter.MyTransactionAdapter;
import com.callpneck.activity.registrationSecond.Model.paymentHistory.PaymentList;
import com.callpneck.activity.registrationSecond.Model.paymentHistory.PaymentListResponse;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsActivity extends AppCompatActivity {

    private TextView tabAllTv,tabMoneyInTv, tabMoneyOutTv, noTransactionTv;
    private RecyclerView allRv, moneyInRv, moneyOutRv;
    private RelativeLayout allRl, moneyInRl, moneyOutRl;
    private SessionManager sessionManager;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_my_transactions);
        init();
        sessionManager=new SessionManager(this);
        user_id = sessionManager.getUserid();
        transactionList = new ArrayList<>();
        showAllUI();
        getTransactionHistory(user_id);

        clickListeners();
        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
    List<PaymentList> transactionList;
    MyTransactionAdapter adapter;
    MoneyInAdapter moneyInAdapter;
    MoneyOutAdapter moneyOutAdapter;
    private void getTransactionHistory(String user_id) {
        transactionList = new ArrayList<>();
        Call<PaymentListResponse> call = APIClient.getInstance().getPaymentList(user_id);
        call.enqueue(new Callback<PaymentListResponse>() {
            @Override
            public void onResponse(Call<PaymentListResponse> call, Response<PaymentListResponse> response) {
                try {

                    PaymentListResponse model = response.body();
                    if (model != null && model.getStatus()){
                        transactionList.clear();
                        transactionList = model.getPaymentList();
                        adapter = new MyTransactionAdapter(TransactionsActivity.this,transactionList);
                        allRv.setAdapter(adapter);
                        showSnackBar(TransactionsActivity.this, model.getMessage());
                    }
                    else if(model != null && !model.getStatus()){
                        showSnackBar(TransactionsActivity.this,model.getMessage());
                    }
                    else {
                        showSnackBar(TransactionsActivity.this,"Server Error");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<PaymentListResponse> call, Throwable t) {

                showSnackBar(TransactionsActivity.this, t.getMessage());
            }
        });
    }
    public static void showSnackBar(Activity activity, String snackTitle) {
        View Parentview=activity.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(Parentview, snackTitle, Snackbar.LENGTH_SHORT);
        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
    }
    private void clickListeners() {
        tabAllTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load all
                showAllUI();
                getTransactionHistory(user_id);
            }
        });
        tabMoneyInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load moneyIn
                showInUI();
                getMoneyInHistory(user_id);
            }
        });
        tabMoneyOutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load moneyOut
                showOutUI();
                getMoneyOutHistory(user_id);
            }
        });
    }

    private void getMoneyOutHistory(String user_id) {
        transactionList = new ArrayList<>();
        Call<PaymentListResponse> call = APIClient.getInstance().getPaymentList(user_id);
        call.enqueue(new Callback<PaymentListResponse>() {
            @Override
            public void onResponse(Call<PaymentListResponse> call, Response<PaymentListResponse> response) {
                try {

                    PaymentListResponse model = response.body();
                    if (model != null && model.getStatus()){
                        transactionList.clear();
                        transactionList = model.getPaymentList();
                        moneyOutAdapter = new MoneyOutAdapter(TransactionsActivity.this,transactionList);
                        moneyOutRv.setAdapter(moneyOutAdapter);
                        showSnackBar(TransactionsActivity.this, model.getMessage());
                    }
                    else if(model != null && !model.getStatus()){
                        showSnackBar(TransactionsActivity.this,model.getMessage());
                    }
                    else {
                        showSnackBar(TransactionsActivity.this,"Server Error");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<PaymentListResponse> call, Throwable t) {

                showSnackBar(TransactionsActivity.this, t.getMessage());
            }
        });
    }

    private void getMoneyInHistory(String user_id) {
        transactionList = new ArrayList<>();
        Call<PaymentListResponse> call = APIClient.getInstance().getPaymentList(user_id);
        call.enqueue(new Callback<PaymentListResponse>() {
            @Override
            public void onResponse(Call<PaymentListResponse> call, Response<PaymentListResponse> response) {
                try {

                    PaymentListResponse model = response.body();
                    if (model != null && model.getStatus()){
                        transactionList.clear();
                        transactionList = model.getPaymentList();
                        moneyInAdapter = new MoneyInAdapter(TransactionsActivity.this,transactionList);
                        moneyInRv.setAdapter(moneyInAdapter);
                        showSnackBar(TransactionsActivity.this, model.getMessage());
                    }
                    else if(model != null && !model.getStatus()){
                        showSnackBar(TransactionsActivity.this,model.getMessage());
                    }
                    else {
                        showSnackBar(TransactionsActivity.this,"Server Error");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<PaymentListResponse> call, Throwable t) {

                showSnackBar(TransactionsActivity.this, t.getMessage());
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
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}