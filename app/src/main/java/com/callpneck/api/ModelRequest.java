package com.callpneck.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelRequest {


    @SerializedName("force")
    @Expose
    private String force;
    @SerializedName("hsn")
    @Expose
    private String hsn;
    @SerializedName("merchantId")
    @Expose
    private String merchantId;

    public String getForce() {
        return force;
    }

    public void setForce(String force) {
        this.force = force;
    }

    public String getHsn() {
        return hsn;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

}
