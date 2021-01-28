package com.callpneck.activity.registrationSecond.fragmentOrder.ModelDelivery;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerOrderData  {

    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_address")
    @Expose
    private String userAddress;
    @SerializedName("user_mobile")
    @Expose
    private Object userMobile;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("signature")
    @Expose
    private String signature;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;
    @SerializedName("dates")
    @Expose
    private String dates;
    @SerializedName("orde_list")
    @Expose
    private String ordeList;
    @SerializedName("order_image")
    @Expose
    private String orderImage;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public Object getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(Object userMobile) {
        this.userMobile = userMobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getOrdeList() {
        return ordeList;
    }

    public void setOrdeList(String ordeList) {
        this.ordeList = ordeList;
    }

    public String getOrderImage() {
        return orderImage;
    }

    public void setOrderImage(String orderImage) {
        this.orderImage = orderImage;
    }

}
