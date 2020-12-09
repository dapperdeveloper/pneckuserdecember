
package com.callpneck.activity.registrationSecond.Model.paymentHistory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentList {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("sender")
    @Expose
    private String sender;
    @SerializedName("recevier")
    @Expose
    private String recevier;
    @SerializedName("sender_name")
    @Expose
    private String senderName;
    @SerializedName("receve_name")
    @Expose
    private String receveName;
    @SerializedName("dates")
    @Expose
    private String dates;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecevier() {
        return recevier;
    }

    public void setRecevier(String recevier) {
        this.recevier = recevier;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceveName() {
        return receveName;
    }

    public void setReceveName(String receveName) {
        this.receveName = receveName;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
