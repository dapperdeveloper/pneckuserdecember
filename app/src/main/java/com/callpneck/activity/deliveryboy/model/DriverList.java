
package com.callpneck.activity.deliveryboy.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverList {

    @SerializedName("driver_list")
    @Expose
    private List<DriverList_> driverList = null;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public List<DriverList_> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<DriverList_> driverList) {
        this.driverList = driverList;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
