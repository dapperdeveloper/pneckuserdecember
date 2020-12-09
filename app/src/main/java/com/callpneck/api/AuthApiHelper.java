package com.callpneck.api;


import com.callpneck.model.dashboard.MainDashboard;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by ankit on 31/10/17.
 */

public interface AuthApiHelper {

    @POST("/connect/")
    Call<ModRes> getRequest(@Body ModelRequest modelRequest, @Header("content-type") String contentType,@Header("Authorization") String Authorization);


    @GET("api/vendorHome")
    Call<MainDashboard>getDashboarddata();


}

