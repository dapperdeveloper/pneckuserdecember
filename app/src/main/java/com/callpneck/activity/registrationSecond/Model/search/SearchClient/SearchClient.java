package com.callpneck.activity.registrationSecond.Model.search.SearchClient;

import android.content.Context;

import com.callpneck.activity.registrationSecond.api.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchClient {
    public static final String BASE_URL = "http://pneck.com/api/";//"http://newlifegroup.co.in/api/";
    private static SearchClient mInstance;
    private Retrofit retrofit;

    private SearchClient(final Context context) {

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

    public static synchronized SearchClient getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new SearchClient(context);
        }
        return mInstance;
    }



    public SearchApiInterface getApi() {
        return retrofit.create(SearchApiInterface.class);
    }
}
