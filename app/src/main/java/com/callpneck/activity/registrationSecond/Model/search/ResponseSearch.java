package com.callpneck.activity.registrationSecond.Model.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseSearch {
    private String title;


    @SerializedName("response")
    @Expose
    private SearchModel response;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SearchModel getResponse() {
        return response;
    }

    public void setResponse(SearchModel response) {
        this.response = response;
    }

}
