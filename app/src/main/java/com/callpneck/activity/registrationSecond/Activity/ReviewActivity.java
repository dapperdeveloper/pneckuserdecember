package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Adapter.AdapterReview;
import com.callpneck.activity.registrationSecond.Model.ModelReview;
import com.callpneck.activity.registrationSecond.Model.ReviewModel.ReviewData;
import com.callpneck.activity.registrationSecond.Model.ReviewModel.ShopReviewRes;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {

    RecyclerView reviewRv;
    List<ReviewData> reviewList;
    AdapterReview adapterReview;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        reviewRv  = findViewById(R.id.reviewRv);
        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if(getIntent()!=null){
            id = getIntent().getStringExtra("id");
        }
        getReviewData(id);
    }

    private void getReviewData(String id) {
        reviewList = new ArrayList<>();
        Call<ShopReviewRes> call = APIClient.getInstance().reviewData(id);
        call.enqueue(new Callback<ShopReviewRes>() {
            @Override
            public void onResponse(Call<ShopReviewRes> call, Response<ShopReviewRes> response) {
                if (response.isSuccessful()){
                    try {
                        ShopReviewRes res = response.body();
                        if (res != null&& res.getSuccess()){
                            if (res.getData().size()>0){
                                reviewList.clear();
                                reviewList = res.getData();
                                adapterReview = new AdapterReview(ReviewActivity.this, reviewList);
                                reviewRv.setAdapter(adapterReview);

                            }else {
                                StyleableToast.makeText(ReviewActivity.this, "No review available at this moment...!", Toast.LENGTH_LONG, R.style.mytoast).show();

                            }
                        }
                    }catch (Exception e){
                        e.toString();
                    }
                }
            }

            @Override
            public void onFailure(Call<ShopReviewRes> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}