package com.callpneck.activity.registrationSecond.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductModel {

    @SerializedName("message")
    @Expose
    private String message;


    @SerializedName("error")
    @Expose
    private String error;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @SerializedName("status")
    @Expose
    private String status;


    public List<Product> getProductList() {
        return product;
    }

    public void setProductList(List<Product> categoryList) {
        this.product = categoryList;
    }

    @SerializedName("product")
    @Expose
    private List<Product> product;
}
