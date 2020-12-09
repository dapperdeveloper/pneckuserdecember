package com.callpneck.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.callpneck.utils.Config;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tibi on 01/07/16.
 */
public class ApiManager {

    private static ApiManager instance = null;

    public AuthApiHelper zippySpotAPI;
    public static final String BASE_URL = "https://bolt.cardpointe.com:6443/api/v2/connect";

    public static final int UNAUTHORIZED = 401;

    public static ApiManager getInstance() {
        if (instance == null)
            instance = new ApiManager();

        return instance;
    }

    public void createApi() {
        // Add detailed logging for requests
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        if (Config.ISDEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }

        // Add headers to each request
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Config.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Config.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Config.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = null;


                            request = chain.request().newBuilder()
                                    .addHeader("Accept", "application/json")
                                    .addHeader("Content-Type", "multipart/form-data")
                                    .addHeader("Authorization", "ZCb8pPkXcZDVO0CIngLSFrBJgA\\/BYyUZIHT8zaj3MPg=")
                                    .build();


                        return chain.proceed(request);
                    }
                })
                .addInterceptor(interceptor)
                .build();

        String url = Config.REST_URL;

        // Set up Retrofit
        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        zippySpotAPI = retrofit.create(AuthApiHelper.class);
    }
}
