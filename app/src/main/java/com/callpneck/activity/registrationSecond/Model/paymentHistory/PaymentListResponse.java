
package com.callpneck.activity.registrationSecond.Model.paymentHistory;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentListResponse {

    @SerializedName("payment_list")
    @Expose
    private List<PaymentList> paymentList = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;

    public List<PaymentList> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<PaymentList> paymentList) {
        this.paymentList = paymentList;
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
