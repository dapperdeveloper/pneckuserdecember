package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.SessionManager;
import com.callpneck.activity.deliveryboy.DeliveryMainActivity;
import com.callpneck.activity.registrationSecond.Adapter.AdapterSearch;
import com.callpneck.activity.registrationSecond.Model.search.ResponseSearch;
import com.callpneck.activity.registrationSecond.Model.search.SearchClient.SearchApiInterface;
import com.callpneck.activity.registrationSecond.Model.search.SearchClient.SearchClient;
import com.callpneck.activity.registrationSecond.Model.search.SearchListModel;
import com.callpneck.taxi.TaxiMainActivity;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    RecyclerView suggestionView, recycleview;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    String query;
    TextView noResult, msg;
    public ProgressBar progressBar;

    List<SearchListModel> searchListModelList;
    AdapterSearch adapterSearch;
    String spokenText;

    EditText ed_search;
    ImageView img_search;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_search);
        suggestionView = findViewById(R.id.suggestionView);
        recycleview = findViewById(R.id.recycleview);
        noResult = findViewById(R.id.noResult);
        msg = findViewById(R.id.msg);
        progressBar = findViewById(R.id.pBar);
        progressBar.setVisibility(View.INVISIBLE);
        ed_search = findViewById(R.id.ed_search);
        img_search = findViewById(R.id.img_search);
        if (getIntent() != null){
            spokenText = getIntent().getStringExtra("spokenText");
            query = spokenText;
            SearchRequest(query);
        }
        sessionManager = new SessionManager(this);
        searchListModelList = new ArrayList<>();
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        suggestionView.setLayoutManager(new LinearLayoutManager(this));
        suggestionView.setVisibility(View.GONE);
        mSwipeRefreshLayout = findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.YELLOW);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                    SearchRequest(query);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                }, 1000);
            }
        });


        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !editable.toString().trim().isEmpty()){
                        query = editable.toString();
                        SearchRequest(query);
                }
            }

        });




    }

    private void SearchRequest(String query) {

        ResponseSearch responseSearch = new ResponseSearch();
        responseSearch.setTitle(query);

        SearchApiInterface apiInterface = SearchClient.getInstance(this).getApi();
        Call<ResponseSearch> call = apiInterface.getResult(responseSearch);
        call.enqueue(new Callback<ResponseSearch>() {
            @Override
            public void onResponse(Call<ResponseSearch> call, Response<ResponseSearch> response) {
                if (response.isSuccessful()){
                    try {

                        ResponseSearch model = response.body();
                        if (model.getResponse().isSuccess() && model.getResponse() != null)
                        {
                            msg.setVisibility(View.GONE);
                            noResult.setVisibility(View.GONE);
                            adapterSearch = new AdapterSearch(SearchActivity.this, model.getResponse().getData().getSearchListModelList(), new AdapterSearch.OnItemClickListener() {
                                @Override
                                public void onItemClick(SearchListModel item) {

                                    if (item.getCate_type().equalsIgnoreCase("restaurant")) {
                                        if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null){
                                            Intent intent = new Intent(SearchActivity.this, ShopHomeActivity.class);
                                            intent.putExtra("categoryName", item.getTitle());
                                            startActivity(intent);
                                           overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                        }


                                    } else if(item.getCate_type().equalsIgnoreCase("cab")){
                                        if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                                            Intent intent = new Intent(SearchActivity.this, TaxiMainActivity.class);
                                            intent.putExtra("categoryName", item.getTitle());
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                        }
                                    }
                                    else if(item.getCate_type().equalsIgnoreCase("provider")){
                                        if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                                            Intent intent = new Intent(SearchActivity.this, ProviderDetailActivity.class);
                                            intent.putExtra("categoryName", item.getTitle());
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                        }
                                    }
                                    else if(item.getCate_type().equalsIgnoreCase("shop")){
                                        if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                                            Intent intent = new Intent(SearchActivity.this, ProviderActivity.class);
                                            intent.putExtra("categoryName", item.getTitle());
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                        }
                                    }
                                    else if(item.getCate_type().equalsIgnoreCase("delivery")){
                                        if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {

                                            if (sessionManager.getCurrentDeliveryOrderId()!=null&&
                                                    sessionManager.getCurrentDeliveryOrderId().length()>0){
                                                LaunchActivityClass.LaunchTrackingDeliveryScreen(SearchActivity.this);
                                            }
                                            else {
                                                Intent intent = new Intent(SearchActivity.this, DeliveryMainActivity.class);
                                                intent.putExtra("categoryName", item.getTitle());
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                            }

                                        }
                                    }
                                    else if(item.getCate_type().equalsIgnoreCase("wallet")){
                                        Intent intent = new Intent(SearchActivity.this, MyWalletActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                    }
                                    else if(item.getCate_type().equalsIgnoreCase("more")){
                                        Intent intent = new Intent(SearchActivity.this, MoreActivity.class);
                                        startActivity(intent);
                                       overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                    }
                                    else {
                                        Toast.makeText(SearchActivity.this, "Location not set", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            recycleview.setAdapter(adapterSearch);
                        }
                        else {

                            msg.setVisibility(View.VISIBLE);
                            noResult.setVisibility(View.GONE);
                        }
                    }catch (Exception e){
                        e.toString();
                        noResult.setVisibility(View.VISIBLE);
                    }
                }


            }

            @Override
            public void onFailure(Call<ResponseSearch> call, Throwable t) {

                msg.setVisibility(View.GONE);
                noResult.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }

}