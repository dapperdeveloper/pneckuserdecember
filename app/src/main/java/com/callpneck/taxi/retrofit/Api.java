package com.callpneck.taxi.retrofit;


import com.callpneck.taxi.model.driver.DriverModel;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @FormUrlEncoded
    @POST("fetchAvailableEmployees")
    Call<DriverModel> getDriver(
            @Field("booking_id") String booking_id
    );


    @POST("fetchAvailableEmployees")
    Call<ResponseBody> cashUp(
            @Query("booking_id") int booking_id,
            @Query("cash_offer") String cash_offer
    );



}
