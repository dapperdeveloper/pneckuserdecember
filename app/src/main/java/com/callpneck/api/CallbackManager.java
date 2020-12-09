package com.callpneck.api;

import java.net.UnknownHostException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ratnad on 8/31/2017.
 */

public abstract class CallbackManager<T> implements Callback<T> {

    private HashMap<String, String> mRequestHeaderMap = new HashMap<>();

    protected CallbackManager() {
        initHeaders();
    }


    private void initHeaders() {
        mRequestHeaderMap.put("app-type", "M");
        mRequestHeaderMap.put("Content-Type", "application/json");
        mRequestHeaderMap.put("Authorization", "");
    }

    @Override
    public void onResponse(Call call, Response response) {
//try{
        if (response.isSuccessful()) {
            onSuccess(response.body());

        } else {
            onError(new RetroError(RetroError.Kind.HTTP, "Unable to connect to server. Try after sometime.", -999));
        }
//}catch (Exception e){
//--
//}
    }

    @Override
    public void onFailure(Call call, Throwable throwable) {
        if (throwable instanceof UnknownHostException) {
            onError(new RetroError(RetroError.Kind.NETWORK, "Unable to connect to server.", -999));
        } else {
            onError(new RetroError(RetroError.Kind.UNEXPECTED, "Unable to connect to server. Try after sometime.", -999));
        }
    }

    protected abstract void onSuccess(Object o);

    protected abstract void onError(RetroError retroError);

}
