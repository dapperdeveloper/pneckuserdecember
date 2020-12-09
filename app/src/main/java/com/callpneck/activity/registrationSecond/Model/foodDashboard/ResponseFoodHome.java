package com.callpneck.activity.registrationSecond.Model.foodDashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseFoodHome {


    @SerializedName("error_code")
    @Expose
    private int error_code;

    @SerializedName("error_string")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private List<Cuisines> cuisines;

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

    public List<Cuisines> getCuisines() {
        return cuisines;
    }

    public void setCuisines(List<Cuisines> cuisines) {
        this.cuisines = cuisines;
    }
}
