
package com.callpneck.activity.registrationSecond.Model.bannerData;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BannerDataResponse {

    @SerializedName("error_code")
    @Expose
    private Integer errorCode;
    @SerializedName("error_string")
    @Expose
    private String errorString;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

}
