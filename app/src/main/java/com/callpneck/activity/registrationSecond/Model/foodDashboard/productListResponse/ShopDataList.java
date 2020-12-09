
package com.callpneck.activity.registrationSecond.Model.foodDashboard.productListResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShopDataList {

    @SerializedName("data")
    @Expose
    private List<ProductList> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public List<ProductList> getData() {
        return data;
    }

    public void setData(List<ProductList> data) {
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
