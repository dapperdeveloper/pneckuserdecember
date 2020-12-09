package com.callpneck.api.retrofit;


import com.callpneck.model.dashboard.MainDashboard;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {


    @GET("api/vendorHome")
    Call<MainDashboard> getDashData();

}
