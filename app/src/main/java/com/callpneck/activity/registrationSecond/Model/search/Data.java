package com.callpneck.activity.registrationSecond.Model.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("searchlist")
    @Expose
    private List<SearchListModel> searchListModelList;


    public List<SearchListModel> getSearchListModelList() {
        return searchListModelList;
    }

    public void setSearchListModelList(List<SearchListModel> searchListModelList) {
        this.searchListModelList = searchListModelList;
    }
}
