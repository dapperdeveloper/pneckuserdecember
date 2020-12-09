
package com.callpneck.taxi.model.driver;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("booking_id")
    @Expose
    private Integer bookingId;
    @SerializedName("employee_id")
    @Expose
    private Integer employeeId;
    @SerializedName("ep_token")
    @Expose
    private String epToken;
    @SerializedName("employee_lat")
    @Expose
    private String employeeLat;
    @SerializedName("employee_lng")
    @Expose
    private String employeeLng;
    @SerializedName("employee_name")
    @Expose
    private String employeeName;
    @SerializedName("employee_phone")
    @Expose
    private String employeePhone;
    @SerializedName("employee_time_to_reach")
    @Expose
    private String employeeTimeToReach;
    @SerializedName("employee_cash_offer")
    @Expose
    private String employeeCashOffer;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getEpToken() {
        return epToken;
    }

    public void setEpToken(String epToken) {
        this.epToken = epToken;
    }

    public String getEmployeeLat() {
        return employeeLat;
    }

    public void setEmployeeLat(String employeeLat) {
        this.employeeLat = employeeLat;
    }

    public String getEmployeeLng() {
        return employeeLng;
    }

    public void setEmployeeLng(String employeeLng) {
        this.employeeLng = employeeLng;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeePhone() {
        return employeePhone;
    }

    public void setEmployeePhone(String employeePhone) {
        this.employeePhone = employeePhone;
    }

    public String getEmployeeTimeToReach() {
        return employeeTimeToReach;
    }

    public void setEmployeeTimeToReach(String employeeTimeToReach) {
        this.employeeTimeToReach = employeeTimeToReach;
    }

    public String getEmployeeCashOffer() {
        return employeeCashOffer;
    }

    public void setEmployeeCashOffer(String employeeCashOffer) {
        this.employeeCashOffer = employeeCashOffer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

}
