
package com.callpneck.activity.registrationSecond.Model.response.responseCategoryList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class YourLocation {

    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("curr_latitude")
    @Expose
    private String currLatitude;
    @SerializedName("curr_longitude")
    @Expose
    private String currLongitude;
    @SerializedName("curr_loc_address")
    @Expose
    private String currLocAddress;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public String getCurrLocAddress() {
        return currLocAddress;
    }

    public void setCurrLocAddress(String currLocAddress) {
        this.currLocAddress = currLocAddress;
    }

}
