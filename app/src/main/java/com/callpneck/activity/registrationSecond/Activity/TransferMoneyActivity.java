package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Model.GetWallet;
import com.callpneck.activity.registrationSecond.Model.RawData;
import com.callpneck.activity.registrationSecond.Model.responseAddMoney.SendMoneyResponse;
import com.callpneck.activity.registrationSecond.Model.sendMoneyResponse.CheckUserForMoney;
import com.callpneck.activity.registrationSecond.api.ApiClient;
import com.callpneck.activity.registrationSecond.api.ApiInterface;
import com.callpneck.activity.registrationSecond.helper.Constant;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferMoneyActivity extends AppCompatActivity {

    Button nextBtn,sendMoneyBtn;
    EditText mailEt,moneyEt;

    String userMailOrNumber, user_id, money, receiverId;
    LinearLayout checkingLayout, paymentLayout;
    ProgressDialog progressDialog;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_transfer_money);

        mailEt = findViewById(R.id.mailEt);
        nextBtn = findViewById(R.id.nextBtn);
        checkingLayout = findViewById(R.id.checkingLayout);
        paymentLayout = findViewById(R.id.paymentLayout);
        moneyEt = findViewById(R.id.moneyEt);
        sendMoneyBtn = findViewById(R.id.sendMoneyBtn);

        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        user_id = sessionManager.getUserid();


        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sendMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                money = moneyEt.getText().toString().trim();
                receiverId = sessionManager.getReceiverId();
                if (TextUtils.isEmpty(money)){
                    Toast.makeText(TransferMoneyActivity.this, "Enter money", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(receiverId)){
                    Toast.makeText(TransferMoneyActivity.this, "Receiver id required", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMoney(money,receiverId);

            }
        });

        mailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable != null && !editable.toString().trim().isEmpty()){
                    getResult(editable.toString());
                }
                else {
                    nextBtn.setEnabled(false);
                    mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mailEt.getText().toString().equals(sessionManager.getUserMail()) || mailEt.getText().toString().equals(sessionManager.getUserMobile())){
                    showSnackBar(TransferMoneyActivity.this, "You can not send money to yourself");
                }else {
                    checkingLayout.setVisibility(View.GONE);
                    paymentLayout.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void sendMoney(String money, String receiverId) {
        progressDialog.show();
        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Call<SendMoneyResponse> call = apiInterface.sendMoneyToUser(user_id, user_id, receiverId, money);
        call.enqueue(new Callback<SendMoneyResponse>() {
            @Override
            public void onResponse(Call<SendMoneyResponse> call, Response<SendMoneyResponse> response) {
                try {
                    SendMoneyResponse model = response.body();
                    if (model != null && model.getStatus()){
                       showSnackBar(TransferMoneyActivity.this,model.getMessage());
                        progressDialog.dismiss();
                        onBackPressed();
                    }
                    else if(model != null && !model.getStatus()){
                        showSnackBar(TransferMoneyActivity.this,model.getMessage());
                        progressDialog.dismiss();
                    }
                    else {
                        showSnackBar(TransferMoneyActivity.this,"Server Error");
                        progressDialog.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<SendMoneyResponse> call, Throwable t) {
                progressDialog.dismiss();
                showSnackBar(TransferMoneyActivity.this,t.getMessage());
            }
        });
    }

    private void getResult(String input) {
        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        Call<CheckUserForMoney> call = apiInterface.checkUserForMoney(input);
        call.enqueue(new Callback<CheckUserForMoney>() {
            @Override
            public void onResponse(Call<CheckUserForMoney> call, Response<CheckUserForMoney> response) {
                CheckUserForMoney model = response.body();
                try {
                    if (model != null && model.getStatus()){
                       mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkmark, 0);
                        sessionManager.setReceiverId(model.getUserData().getId()+"");
                        nextBtn.setEnabled(true);
                    }
                    else if(model != null && !model.getStatus()){
                        nextBtn.setEnabled(false);
                        mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        showSnackBar(TransferMoneyActivity.this, model.getMessage());
                    }
                    else {
                        nextBtn.setEnabled(false);
                        mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        showSnackBar(TransferMoneyActivity.this, "Server Error");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<CheckUserForMoney> call, Throwable t) {
                mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                nextBtn.setEnabled(false);
                showSnackBar(TransferMoneyActivity.this, t.getMessage());
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
    private boolean validation(){
        boolean valid = true;
        userMailOrNumber = mailEt.getText().toString();
        if(TextUtils.isEmpty(userMailOrNumber)){
            Toast.makeText(this, "Enter mail or mobile", Toast.LENGTH_SHORT).show();
            return  false;
        }

        return valid;
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200);

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

    }

}