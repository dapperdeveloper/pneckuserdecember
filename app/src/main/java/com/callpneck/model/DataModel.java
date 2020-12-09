package com.callpneck.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataModel {

    public Integer getCreate_otp() {
        return create_otp;
    }

    public void setCreate_otp(Integer create_otp) {
        this.create_otp = create_otp;
    }

    @SerializedName("create_otp")
    @Expose
    private Integer create_otp = null;

    @SerializedName("user_id")
    @Expose
    private Integer user_id = null;

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

}
