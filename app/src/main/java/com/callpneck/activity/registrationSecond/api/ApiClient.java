package com.callpneck.activity.registrationSecond.api;

import android.content.Context;

import com.callpneck.api.retrofit.Api;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL = "https://pneck.in/api/";
    private static ApiClient mInstance;
    private Retrofit retrofit;

    private ApiClient(final Context context) {

        if (retrofit == null) {

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build();
        }
    }

    public static synchronized ApiClient getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new ApiClient(context);
        }
        return mInstance;
    }



    public ApiInterface getApi() {
        return retrofit.create(ApiInterface.class);
    }
}
