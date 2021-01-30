
package com.callpneck.activity.registrationSecond.Model.ReviewModel;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShopReviewRes {

    @SerializedName("data")
    @Expose
    private List<ReviewData> data = null;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public List<ReviewData> getData() {
        return data;
    }

    public void setData(List<ReviewData> data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
