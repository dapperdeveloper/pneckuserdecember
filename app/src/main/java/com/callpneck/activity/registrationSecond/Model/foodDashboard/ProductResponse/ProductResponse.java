package com.callpneck.activity.registrationSecond.Model.foodDashboard.ProductResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductResponse {

    @SerializedName("error_code")
    @Expose
    private int error_code;

    @SerializedName("error_string")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private List<ProductFood> productFoodList;


    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ProductFood> getProductFoodList() {
        return productFoodList;
    }

    public void setProductFoodList(List<ProductFood> productFoodList) {
        this.productFoodList = productFoodList;
    }



}
