package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.activity.registrationSecond.Adapter.AdapterSearch;
import com.callpneck.activity.registrationSecond.Model.search.ResponseSearch;
import com.callpneck.activity.registrationSecond.Model.search.SearchClient.SearchApiInterface;
import com.callpneck.activity.registrationSecond.Model.search.SearchClient.SearchClient;
import com.callpneck.activity.registrationSecond.Model.search.SearchListModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    RecyclerView suggestionView, recycleview;
    SearchView searchView;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    String query;
    TextView noResult, msg;
    public ProgressBar progressBar;

    List<SearchListModel> searchListModelList;
    AdapterSearch adapterSearch;
    String spokenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_search);

        if (getIntent() != null){
            spokenText = getIntent().getStringExtra("spokenText");
            Toast.makeText(this, ""+spokenText, Toast.LENGTH_SHORT).show();
            if ((spokenText != null &&
                    spokenText.length() > 0)){

            }

        }
        suggestionView = findViewById(R.id.suggestionView);
        recycleview = findViewById(R.id.recycleview);
        searchView = findViewById(R.id.searchview);
        noResult = findViewById(R.id.noResult);
        msg = findViewById(R.id.msg);
        progressBar = findViewById(R.id.pBar);
        progressBar.setVisibility(View.INVISIBLE);

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



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("") || newText.length() != 0) {
                    query = newText;
                        SearchRequest(newText);
                }
                return false;
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
                ResponseSearch model = response.body();
                if (model.getResponse().isSuccess() && model.getResponse() != null)
                {
                    adapterSearch = new AdapterSearch(SearchActivity.this, model.getResponse().getData().getSearchListModelList(), new AdapterSearch.OnItemClickListener() {
                        @Override
                        public void onItemClick(SearchListModel item) {

                        }
                    });
                    recycleview.setAdapter(adapterSearch);
                }
                else {
                    if (model == null){
                        Toast.makeText(SearchActivity.this, ""+model.getResponse().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(SearchActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSearch> call, Throwable t) {

                Toast.makeText(SearchActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }

}