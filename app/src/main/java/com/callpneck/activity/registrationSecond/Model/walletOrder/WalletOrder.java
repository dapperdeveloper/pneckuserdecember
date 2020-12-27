
package com.callpneck.activity.registrationSecond.Model.walletOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletOrder {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("success")
    @Expose
    private Boolean success;


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
