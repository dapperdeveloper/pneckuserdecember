package com.callpneck.activity.registrationSecond.fragmentOrder.ModelDelivery;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustumerOrderDetail{

    @SerializedName("data")
    @Expose
    private CustomerOrderData data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;

    public CustomerOrderData getData() {
        return data;
    }

    public void setData(CustomerOrderData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
