
package com.callpneck.activity.deliveryboy.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverList_ {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("emp_address")
    @Expose
    private String empAddress;
    @SerializedName("rating")
    @Expose
    private Object rating;
    @SerializedName("curr_latitude")
    @Expose
    private String currLatitude;
    @SerializedName("curr_longitude")
    @Expose
    private String currLongitude;
    @SerializedName("vehicle_image")
    @Expose
    private String vehicleImage;
    @SerializedName("distance")
    @Expose
    private Double distance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmpAddress() {
        return empAddress;
    }

    public void setEmpAddress(String empAddress) {
        this.empAddress = empAddress;
    }

    public Object getRating() {
        return rating;
    }

    public void setRating(Object rating) {
        this.rating = rating;
    }

    public String getCurrLatitude() {
        return currLatitude;
    }

    public void setCurrLatitude(String currLatitude) {
        this.currLatitude = currLatitude;
    }

    public String getCurrLongitude() {
        return currLongitude;
    }

    public void setCurrLongitude(String currLongitude) {
        this.currLongitude = currLongitude;
    }

    public String getVehicleImage() {
        return vehicleImage;
    }

    public void setVehicleImage(String vehicleImage) {
        this.vehicleImage = vehicleImage;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

}
