package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Adapter.PneckUserListAdapter;
import com.callpneck.activity.registrationSecond.Model.responseAddMoney.SendMoneyResponse;
import com.callpneck.activity.registrationSecond.Model.sendMoneyResponse.CheckUserForMoney;
import com.callpneck.activity.registrationSecond.Model.userList.PneckList;
import com.callpneck.activity.registrationSecond.Model.userList.PneckUserList;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;
import com.callpneck.taxi.TaxiMainActivity;
import com.callpneck.taxi.activity.DriverListActivity;
import com.callpneck.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferMoneyActivity extends AppCompatActivity {

    Button nextBtn;
    EditText mailEt;

    String user_id, money, receiverId;
    ProgressDialog progressDialog;
    SessionManager sessionManager;
    RecyclerView pneckUserRv;
    PneckUserListAdapter adapter;
    List<PneckList> pneckLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_transfer_money);

        mailEt = findViewById(R.id.mailEt);
        nextBtn = findViewById(R.id.nextBtn);
        pneckUserRv = findViewById(R.id.pneckUserRv);

        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        user_id = sessionManager.getUserid();

        pneckLists = new ArrayList<>();

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
                    getPneckUserList(editable.toString());
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
                if (mailEt.getText().toString().trim().equals(sessionManager.getUserMail()) || mailEt.getText().toString().equals(sessionManager.getUserMobile())){
                    StyleableToast.makeText(TransferMoneyActivity.this, "You can not send money to yourself", Toast.LENGTH_LONG, R.style.mytoast).show();
                }else {
                    moneyDialog();
                }
            }
        });

        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void getPneckUserList(String input) {
        Call<PneckUserList> call = APIClient.getInstance().getPneckUserList(input);
        call.enqueue(new Callback<PneckUserList>() {
            @Override
            public void onResponse(Call<PneckUserList> call, Response<PneckUserList> response) {
                try {
                    PneckUserList model = response.body();
                    if(model != null && model.getStatus()){
                        pneckLists.clear();
                        pneckLists = model.getPneckList();
                        adapter = new PneckUserListAdapter(getApplicationContext(), pneckLists, new PneckUserListAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(PneckList item) {
                                String mobile = item.getMobile();
                                mailEt.setText(mobile);
                            }
                        });
                        pneckUserRv.setAdapter(adapter);
                    }
                    else if (model != null && !model.getStatus()){
                        StyleableToast.makeText(TransferMoneyActivity.this, model.getMessage(), Toast.LENGTH_LONG, R.style.mytoast).show();
                    }
                    else {
                        StyleableToast.makeText(TransferMoneyActivity.this, "Server Error", Toast.LENGTH_LONG, R.style.mytoast).show();

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<PneckUserList> call, Throwable t) {

            }
        });
    }

    private void moneyDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.botttom_dialog_send_money,null);
        bottomSheetDialog.setContentView(view);
        SessionManager sessionManager = new SessionManager(this);

        EditText moneyEt =  view.findViewById(R.id.moneyEt);
        Button  sendMoneyBtn = view.findViewById(R.id.sendMoneyBtn);

        bottomSheetDialog.show();
        sendMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                money = moneyEt.getText().toString().trim();
                receiverId = sessionManager.getReceiverId();
                user_id = sessionManager.getUserid();
                if (TextUtils.isEmpty(money)){
                    StyleableToast.makeText(TransferMoneyActivity.this, "Enter Money", Toast.LENGTH_LONG, R.style.mytoast).show();
                    return;
                }
                if (TextUtils.isEmpty(receiverId)){
                    StyleableToast.makeText(TransferMoneyActivity.this, "Receiver id required", Toast.LENGTH_LONG, R.style.mytoast).show();
                    return;
                }
                sendMoney(user_id,money,receiverId);
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void sendMoney(String user_id,String money, String receiverId) {
        progressDialog.show();
        Call<SendMoneyResponse> call = APIClient.getInstance().sendMoneyToUser(user_id, user_id, receiverId, money);
        call.enqueue(new Callback<SendMoneyResponse>() {
            @Override
            public void onResponse(Call<SendMoneyResponse> call, Response<SendMoneyResponse> response) {
                try {
                    SendMoneyResponse model = response.body();
                    if (model != null && model.getStatus()){


                        //noti
                        String message = "You have received money!";
                        prepareNotificationMessage(message);
                    }
                    else if(model != null && !model.getStatus()){
                        StyleableToast.makeText(TransferMoneyActivity.this, model.getMessage(), Toast.LENGTH_LONG, R.style.mytoast).show();
                        progressDialog.dismiss();
                    }
                    else {
                        StyleableToast.makeText(TransferMoneyActivity.this, "Server Error", Toast.LENGTH_LONG, R.style.mytoast).show();
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
            }
        });
    }


    private void prepareNotificationMessage(String message){


        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "Money Received....!";
        String NOTIFICATION_MESSAGE = ""+ message;
        String NOTIFICATION_TYPE = "MoneyReceived";

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try {
            //what to send
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid",sessionManager.getUserid()); //current user is user so uid is buyer id
            notificationBodyJo.put("sellerUid",receiverId);
            notificationBodyJo.put("orderId","878878");
            notificationBodyJo.put("notificationTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage",NOTIFICATION_MESSAGE);

            //where to send
            notificationJo.put("to",NOTIFICATION_TOPIC);
            notificationJo.put("data",notificationBodyJo);

        }catch (Exception e){

            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJo);
    }

    private void sendFcmNotification(JSONObject notificationJo) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("NOTIFICATION", response.toString());
                        progressDialog.dismiss();
                        sessionManager.setReceiverId("");
                        onBackPressed();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        sessionManager.setReceiverId("");
                        onBackPressed();
                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization","key="+Constants.FCM_KEY);
                return headers;
            }
        };

        //enqueue the Volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }


    private void getResult(String input) {
        Call<CheckUserForMoney> call = APIClient.getInstance().checkUserForMoney(input);
        call.enqueue(new Callback<CheckUserForMoney>() {
            @Override
            public void onResponse(Call<CheckUserForMoney> call, Response<CheckUserForMoney> response) {
                CheckUserForMoney model = response.body();
                try {
                    if (model != null && model.getStatus()){
                       mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkmark, 0);
                        sessionManager.setReceiverId(model.getUserData().getId()+"");
                        Log.e("RECEIVER_ID", sessionManager.getReceiverId());
                        nextBtn.setEnabled(true);
                    }
                    else if(model != null && !model.getStatus()){
                        nextBtn.setEnabled(false);
                        mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                    else {
                        nextBtn.setEnabled(false);
                        mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        StyleableToast.makeText(TransferMoneyActivity.this, "Server Error", Toast.LENGTH_LONG, R.style.mytoast).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<CheckUserForMoney> call, Throwable t) {
                mailEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                nextBtn.setEnabled(false);
            }
        });
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
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }

}