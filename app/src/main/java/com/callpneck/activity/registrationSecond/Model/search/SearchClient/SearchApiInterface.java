package com.callpneck.activity.registrationSecond.Model.search.SearchClient;


import com.callpneck.activity.registrationSecond.Model.foodDashboard.ProductResponse.ProductResponse;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ResponseFoodHome;
import com.callpneck.activity.registrationSecond.Model.search.ResponseSearch;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SearchApiInterface {


    @POST("search")
    Call<ResponseSearch>  getResult(@Body ResponseSearch search);

}
