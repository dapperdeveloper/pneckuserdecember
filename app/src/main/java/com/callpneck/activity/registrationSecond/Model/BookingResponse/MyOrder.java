
package com.callpneck.activity.registrationSecond.Model.BookingResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyOrder {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("accept_otp_confirm_at")
    @Expose
    private String acceptOtpConfirmAt;
    @SerializedName("delivery_confirm_at")
    @Expose
    private String deliveryConfirmAt;
    @SerializedName("order_subtotal")
    @Expose
    private Integer orderSubtotal;
    @SerializedName("booking_charge")
    @Expose
    private Integer bookingCharge;
    @SerializedName("grand_total")
    @Expose
    private Integer grandTotal;
    @SerializedName("booking_complete_at")
    @Expose
    private String bookingCompleteAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAcceptOtpConfirmAt() {
        return acceptOtpConfirmAt;
    }

    public void setAcceptOtpConfirmAt(String acceptOtpConfirmAt) {
        this.acceptOtpConfirmAt = acceptOtpConfirmAt;
    }

    public String getDeliveryConfirmAt() {
        return deliveryConfirmAt;
    }

    public void setDeliveryConfirmAt(String deliveryConfirmAt) {
        this.deliveryConfirmAt = deliveryConfirmAt;
    }

    public Integer getOrderSubtotal() {
        return orderSubtotal;
    }

    public void setOrderSubtotal(Integer orderSubtotal) {
        this.orderSubtotal = orderSubtotal;
    }

    public Integer getBookingCharge() {
        return bookingCharge;
    }

    public void setBookingCharge(Integer bookingCharge) {
        this.bookingCharge = bookingCharge;
    }

    public Integer getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Integer grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getBookingCompleteAt() {
        return bookingCompleteAt;
    }

    public void setBookingCompleteAt(String bookingCompleteAt) {
        this.bookingCompleteAt = bookingCompleteAt;
    }

}
