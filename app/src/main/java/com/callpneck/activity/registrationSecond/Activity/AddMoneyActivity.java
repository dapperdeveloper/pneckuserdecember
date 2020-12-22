package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Model.responseAddMoney.AddMoneyResponse;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;
import com.callpneck.activity.registrationSecond.helper.Constant;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMoneyActivity extends AppCompatActivity implements PaymentResultListener {

    private static final String TAG = CheckoutActivity.class.getSimpleName();
    View top_view;
    ImageView decreaseBtn, increaseBtn;
    EditText moneyTv;
    CardView hundredBtn, threeHundredBtn, fiveHundredBtn;
    Button addMoneyBtn;
    public String razorPayId;
    private SessionManager sessionManager;
    String user_id,  userName ,userMobile, userMail;
    int money;
    String totalAmount;
    String[] amount;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_add_money);

        top_view=findViewById(R.id.top_view);
        decreaseBtn=findViewById(R.id.decreaseBtn);
        increaseBtn=findViewById(R.id.increaseBtn);
        moneyTv=findViewById(R.id.moneyTv);
        hundredBtn=findViewById(R.id.hundredBtn);
        threeHundredBtn=findViewById(R.id.threeHundredBtn);
        fiveHundredBtn=findViewById(R.id.fiveHundredBtn);
        addMoneyBtn=findViewById(R.id.addMoneyBtn);

        Checkout.preload(getApplicationContext());
        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait.....");
        progressDialog.setCanceledOnTouchOutside(false);
        userName = sessionManager.getUserName();
        user_id = sessionManager.getUserid();
        userMobile = sessionManager.getUserMobile();
        userMail = sessionManager.getUserMail();

        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        clickListener();

    }

    private void clickListener() {
        hundredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                money = 100;
                display(money);
            }
        });
        threeHundredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                money = 300;
                display(money);
            }
        });
        fiveHundredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                money = 500;
                display(money);
            }
        });
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                money = money + 1;
                display(money);
            }
        });
        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (money>0){
                    money = money - 1;
                    display(money);
                }
            }
        });

        addMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()){
                    startAmount();
                }
            }
        });
    }
    private boolean validation(){
        boolean valid = true;
        totalAmount = moneyTv.getText().toString().trim();
        if (TextUtils.isEmpty(totalAmount)) {
            Toast.makeText(AddMoneyActivity.this, "Please add some money!", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if (TextUtils.isEmpty(user_id)) {
            Toast.makeText(AddMoneyActivity.this, "User Does not exist!", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if (TextUtils.isEmpty(userName)) {
            Toast.makeText(AddMoneyActivity.this, "Check your name", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if (TextUtils.isEmpty(userMail)) {
            Toast.makeText(AddMoneyActivity.this, "Add Email in Profile", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if (TextUtils.isEmpty(userMobile)) {
            Toast.makeText(AddMoneyActivity.this, "Please add Mobile!", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }
    private void startAmount() {
        amount = String.valueOf(Double.valueOf(totalAmount) * 100).split("\\.");
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_xbt5pRoHOuHjZl");
        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.pneck_logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();
            options.put(Constant.NAME, userName);
            options.put(Constant.CURRENCY, "INR");
            options.put(Constant.AMOUNT, ""+amount[0]);
            JSONObject preFill = new JSONObject();
            preFill.put(Constant.EMAIL,userMail);
            preFill.put(Constant.CONTACT, userMobile);
            options.put("prefill", preFill);
            //pass amount in currency subunits
            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }

    }
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        /**
         * Add your logic here for a successful payment response
         */
        try {
            razorPayId = razorpayPaymentID;
            if (razorPayId!=null)
            addMoneyApi(""+razorPayId,""+user_id, ""+userName, ""+userMail, ""+userMobile, ""+totalAmount);

        } catch (Exception e) {
            Log.d(TAG, "onPaymentSuccess  ", e);
        }

    }

    private void addMoneyApi(String razorPayId, String user_id, String userName, String userMail,String userMobile, String totalAmount) {
        progressDialog.show();
        Call<AddMoneyResponse> call = APIClient.getInstance().addMoney(user_id, userName, userMail, userMobile, totalAmount,razorPayId);
        call.enqueue(new Callback<AddMoneyResponse>() {
            @Override
            public void onResponse(Call<AddMoneyResponse> call, Response<AddMoneyResponse> response) {
                AddMoneyResponse response1 = response.body();
                if (response1!= null && response1.getStatus()){
                    Toast.makeText(AddMoneyActivity.this, ""+response1.getMessage(), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    progressDialog.dismiss();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(AddMoneyActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddMoneyResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onPaymentError(int code, String response) {
        /**
         * Add your logic here for a failed payment response
         */
        try {
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d(TAG, "onPaymentError  ", e);
        }
    }

    private void display(int money) {
        moneyTv.setText(""+money);
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200);
        top_view.startAnimation(anim);
        top_view.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        top_view.setVisibility(View.GONE);
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }

}