package com.callpneck.utils;

import com.callpneck.model.LoginModel;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("UserOtpSend")
    @Headers("Accept:application/vnd.yourapi.v1.full+json")
    Call<LoginModel> otpsend(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("UserMobileVerify")
    @Headers("Accept:application/vnd.yourapi.v1.full+json")
    Call<LoginModel> verifyotp(@Field("mobile") String mobile,@Field("otp") String otp,@Field("device_token") String devicetoken);


    @FormUrlEncoded
    @POST("UserRegister")
    @Headers("Accept:application/vnd.yourapi.v1.full+json")
    Call<LoginModel> regUser(@Field("id") String id,@Field("name") String name,@Field("email") String email);
}
