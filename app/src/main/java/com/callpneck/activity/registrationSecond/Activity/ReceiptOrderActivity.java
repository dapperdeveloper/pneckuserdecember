package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.callpneck.Language.LanguageSettingActivity;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.OrderCompleteHappyScreen;
import com.callpneck.activity.PneckMapLocation;
import com.callpneck.activity.TrackOrder.Model.Item;
import com.callpneck.activity.TrackOrder.Model.TrackOrder;
import com.callpneck.activity.TrackOrder.Model.TrackOrderModel;
import com.callpneck.activity.registrationSecond.MainScreenActivity;
import com.callpneck.activity.registrationSecond.Model.addContact.DeleteContact;
import com.callpneck.activity.registrationSecond.Model.response.responseOrder.OrderUser;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.utils.CustPrograssbar;
import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiptOrderActivity extends AppCompatActivity {

    TextView tvItemTotal, tvTaxPercent, tvTaxAmt, tvDeliveryCharge, tvTotal, tvPromoCode, tvPCAmount, tvWallet, tvFinalTotal, tvDPercent, tvDAmount;
    TextView txtcanceldetail, txtotherdetails, txtorderid, txtorderdate;
    public static ProgressBar pBar;
    public static Button btnCancel;
    public static LinearLayout lyttracker;
    View l4;
    LinearLayout returnLyt, lytPromo, lytWallet, lytPriceDetail;
    double totalAfterTax = 0.0;
    String oid, type;
    CustPrograssbar custPrograssbar;
    LinearLayout lvlItems;
    List<Item> productinfoArrayList;
    String order_id, res_id;
    Activity activity=this;
    SessionManager sessionManager;

    LinearLayout commentLayout;
    RatingBar ratingBar;
    EditText userFeedBack;
    TextView CancelDialog;
    TextView SubmitRating;
    RelativeLayout ratingLayout;

    private void init() {
        lvlItems = findViewById(R.id.lvl_items);
        pBar = findViewById(R.id.pBar);
        lytPriceDetail = findViewById(R.id.lytPriceDetail);
        lytWallet = findViewById(R.id.lytWallet);
        tvItemTotal = findViewById(R.id.tvItemTotal);
        tvTaxPercent = findViewById(R.id.tvTaxPercent);
        tvTaxAmt = findViewById(R.id.tvTaxAmt);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvTotal = findViewById(R.id.tvTotal);
        /*
        tvDAmount = findViewById(R.id.tvDAmount);
        tvDPercent = findViewById(R.id.tvDPercent);
        lytPromo = findViewById(R.id.lytPromo);
        tvPromoCode = findViewById(R.id.tvPromoCode);
        tvPCAmount = findViewById(R.id.tvPCAmount);

         */
        tvWallet = findViewById(R.id.tvWallet);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);
        txtorderid = findViewById(R.id.txtorderid);
        txtorderdate = findViewById(R.id.txtorderdate);

        txtotherdetails = findViewById(R.id.txtotherdetails);
        txtcanceldetail = findViewById(R.id.txtcanceldetail);
        lyttracker = findViewById(R.id.lyttracker);
        btnCancel = findViewById(R.id.btncancel);
        l4 = findViewById(R.id.l4);

        returnLyt = findViewById(R.id.returnLyt);
        commentLayout = findViewById(R.id.comment_layout);
        ratingBar = findViewById(R.id.user_rating);
        userFeedBack = findViewById(R.id.user_feed_back_comment);

        CancelDialog = (TextView) findViewById(R.id.Cancel_dialog);
        SubmitRating = (TextView) findViewById(R.id.submit_rating);
        ratingLayout = findViewById(R.id.hj);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_receipt_order);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Receipt");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        init();

        oid = getIntent().getStringExtra("status");
        type = getIntent().getStringExtra("type");
        sessionManager = new SessionManager(this);
        custPrograssbar = new CustPrograssbar();
        productinfoArrayList = new ArrayList<>();
        if (oid!=null)
        getOrderDetails(oid, type);

        clickListener();

    }

    private void clickListener() {
        SubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() > 0) {

                    if (userFeedBack.getText().toString().length()<10){
                        Toast.makeText(activity,"Feedback should be larger the 10 words",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    submitRatingToServer(ratingBar.getRating(), userFeedBack.getText().toString());

                } else {
                    Toast.makeText(ReceiptOrderActivity.this, "Please provide your feedback", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void submitRatingToServer(float rating, String feedback) {

        custPrograssbar.PrograssCreate(this);
        Call<DeleteContact>  call = APIClient.getInstance().ratingToVendor(sessionManager.getUserid(), oid, res_id, feedback, rating+"");

        call.enqueue(new Callback<DeleteContact>() {
            @Override
            public void onResponse(Call<DeleteContact> call, Response<DeleteContact> response) {
                if (response.isSuccessful()){
                    custPrograssbar.ClosePrograssBar();
                    try {
                        DeleteContact deleteContact = response.body();
                        if (deleteContact.getSuccess()){
                            StyleableToast.makeText(ReceiptOrderActivity.this, deleteContact.getMessage(), Toast.LENGTH_LONG, R.style.mytoast).show();
                            onBackPressed();
                        }
                    }catch (Exception e){
                        e.toString();
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteContact> call, Throwable t) {
                custPrograssbar.ClosePrograssBar();
            }
        });
    }


    private void getOrderDetails(String oid, String type) {

        custPrograssbar.PrograssCreate(this);
        Call<TrackOrderModel> call = APIClient.getInstance().UserOrderListDetails(oid, type);
        call.enqueue(new Callback<TrackOrderModel>() {
            @Override
            public void onResponse(Call<TrackOrderModel> call, Response<TrackOrderModel> response) {
                if (response.isSuccessful()){
                    custPrograssbar.ClosePrograssBar();
                    try {
                        TrackOrderModel model = response.body();
                        productinfoArrayList.clear();
                        productinfoArrayList = model.getData().getItem();
                        setJoinPlayrList(lvlItems, productinfoArrayList);
                        TrackOrder trackOrder = model.getData();
                        setDataToUI(trackOrder);

                        if (trackOrder.getRating().equalsIgnoreCase("false")){
                            ratingLayout.setVisibility(View.VISIBLE);
                        }else {
                            ratingLayout.setVisibility(View.GONE);
                        }
                        if (model.getMessage().equals("Shop")){
                            if (!model.getData().getStatus().equalsIgnoreCase("Preparing")&&!model.getData().getStatus().equalsIgnoreCase("On The Way")&&!model.getData().getStatus().equalsIgnoreCase("delivered") && !model.getData().getStatus().equalsIgnoreCase("Cancelled") && !model.getData().getStatus().equalsIgnoreCase("returned")) {
                                btnCancel.setVisibility(View.VISIBLE);
                            } else {
                                btnCancel.setVisibility(View.GONE);
                            }
                        }else {
                            btnCancel.setVisibility(View.GONE);
                        }

                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onFailure(Call<TrackOrderModel> call, Throwable t) {
                custPrograssbar.ClosePrograssBar();
            }
        });
    }

    private void setDataToUI(TrackOrder data) {
        res_id = data.getResId()+"";
        order_id = data.getId()+"";
        tvFinalTotal.setText(data.getTotalAmount()+"");
        tvItemTotal.setText(data.getTotalAmount()+"");
        txtotherdetails.setText(getString(R.string.name_1) + data.getName() + getString(R.string.mobile_no_1) + data.getMobile() + getString(R.string.address_1) + data.getUsrAddress());
        String[] date = data.getDates().split("\\s+");
        txtorderid.setText(data.getOrderNumber());
        txtorderdate.setText(date[0]);
        if (data.getStatus().equalsIgnoreCase("cancelled")) {
            lyttracker.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            txtcanceldetail.setVisibility(View.VISIBLE);
            txtcanceldetail.setText(data.getMessage());
            lytPriceDetail.setVisibility(View.GONE);
        } else {
            lytPriceDetail.setVisibility(View.VISIBLE);
            if (data.getStatus().equals("returned")) {
                l4.setVisibility(View.VISIBLE);
                returnLyt.setVisibility(View.VISIBLE);
            }
            lyttracker.setVisibility(View.VISIBLE);



        }

    }

    private void setJoinPlayrList(LinearLayout lnrView, List<Item> productinfoArrayList) {
        lnrView.removeAllViews();
        for (int i = 0; i < productinfoArrayList.size(); i++) {
            Item listdatum = productinfoArrayList.get(i);
            LayoutInflater inflater = LayoutInflater.from(ReceiptOrderActivity.this);

            View view = inflater.inflate(R.layout.pending_order_item, null);
            ImageView imgView = view.findViewById(R.id.imageView);
            TextView txtTitle = view.findViewById(R.id.txt_title);
            TextView txtItems = view.findViewById(R.id.txt_items);
            TextView txt_wight = view.findViewById(R.id.txt_wight);
            TextView txt_price = view.findViewById(R.id.txt_price);
            TextView txt_pricedis = view.findViewById(R.id.txt_pricedis);

            txtTitle.setText("" + listdatum.getName());
            txtItems.setText(new StringBuilder("x ").append(listdatum.getQuantity()).append(" items"));
            Glide.with(this).load(listdatum.getImage()).placeholder(R.drawable.ic_account).into(imgView);
            txt_price.setText(new StringBuilder("Rs.").append(listdatum.getCost()));
            txt_pricedis.setText(new StringBuilder("Rs.").append(listdatum.getPrice()));


            lnrView.addView(view);
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);
    }
    private boolean isCancelDialogOpen=false;
    String message="";
    public void OnBtnClick(View view) {
        int id = view.getId();
        if (id == R.id.btncancel) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View mView = activity.getLayoutInflater().inflate(R.layout.cancel_order_dialog, null);
            TextInputEditText cancelReason;
            TextView stayOnOrder;
            TextView cancelOrder;
            RadioButton oneRb, twoRb, threeRb, fourRb, fiveRb, otherRb;
            oneRb = mView.findViewById(R.id.oneRb);
            twoRb = mView.findViewById(R.id.twoRb);
            threeRb = mView.findViewById(R.id.threeRb);
            fourRb = mView.findViewById(R.id.fourRb);
            fiveRb = mView.findViewById(R.id.fiveRb);
            otherRb = mView.findViewById(R.id.otherRb);
            cancelReason = (TextInputEditText)mView.findViewById( R.id.cancel_reason );
            stayOnOrder = (TextView)mView.findViewById( R.id.stay_on_order );
            cancelOrder = (TextView)mView.findViewById( R.id.cancel_order );

            builder.setView(mView);
            final AlertDialog NewCategory_dialog = builder.create();

            NewCategory_dialog.setCancelable(false);
            isCancelDialogOpen=true;

            oneRb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    message = oneRb.getText().toString();
                    cancelReason.setVisibility(View.GONE);
                }
            });
            twoRb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    message = twoRb.getText().toString();
                    cancelReason.setVisibility(View.GONE);
                }
            });
            threeRb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    message = threeRb.getText().toString();
                    cancelReason.setVisibility(View.GONE);
                }
            });
            fourRb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    message = fourRb.getText().toString();
                    cancelReason.setVisibility(View.GONE);
                }
            });
            fiveRb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    message = fiveRb.getText().toString();
                    cancelReason.setVisibility(View.GONE);
                }
            });
            otherRb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    message = otherRb.getText().toString();
                    cancelReason.setVisibility(View.GONE);
                }
            });
            cancelOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (message.isEmpty()){
                        Toast.makeText(activity,getString(R.string.PLEASE_ENTER_CANCEL_RESON),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cancelOrderCall();
                    NewCategory_dialog.dismiss();
                }
            });
            stayOnOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    message="";
                    isCancelDialogOpen=false;
                    NewCategory_dialog.dismiss();
                }
            });

            NewCategory_dialog.show();
        }
    }

    private void cancelOrderCall() {
        custPrograssbar.PrograssCreate(this);
        Call<DeleteContact> call = APIClient.getInstance().CancelOrder(oid, sessionManager.getUserid(), res_id, message);
        call.enqueue(new Callback<DeleteContact>() {
            @Override
            public void onResponse(Call<DeleteContact> call, Response<DeleteContact> response) {
                if (response.isSuccessful()){
                    custPrograssbar.ClosePrograssBar();
                    try {
                        DeleteContact deleteContact = response.body();
                        if (deleteContact.getSuccess()){
                            Intent refresh = new Intent(ReceiptOrderActivity.this, ReceiptOrderActivity.class);
                            refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Set this flag
                            startActivity(refresh);
                        }
                    }catch (Exception e){
                        e.toString();
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteContact> call, Throwable t) {
                custPrograssbar.ClosePrograssBar();
            }
        });
    }
}
