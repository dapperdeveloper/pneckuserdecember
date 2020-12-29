
package com.callpneck.activity.TrackOrder.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackOrderModel {

    @SerializedName("data")
    @Expose
    private TrackOrder data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public TrackOrder getData() {
        return data;
    }

    public void setData(TrackOrder data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
