
package com.callpneck.activity.deliveryboy.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("emp_name")
    @Expose
    private String empName;
    @SerializedName("emp_mobile")
    @Expose
    private String empMobile;
    @SerializedName("emp_profile")
    @Expose
    private String empProfile;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("total_amount")
    @Expose
    private String total_amount;
    @SerializedName("time")
    @Expose
    private String time;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpMobile() {
        return empMobile;
    }

    public void setEmpMobile(String empMobile) {
        this.empMobile = empMobile;
    }

    public String getEmpProfile() {
        return empProfile;
    }

    public void setEmpProfile(String empProfile) {
        this.empProfile = empProfile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }
}
