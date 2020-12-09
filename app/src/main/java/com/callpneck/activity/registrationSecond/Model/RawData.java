package com.callpneck.activity.registrationSecond.Model;

import com.callpneck.activity.Database.MainData;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RawData {

    @SerializedName("items")
    private List<MainData> data;

    public RawData(List<MainData> data) {
        this.data = data;
    }


    public List<MainData> getData() {
        return data;
    }

    public void setData(List<MainData> data) {
        this.data = data;
    }
}
