
package com.callpneck.activity.registrationSecond.Model.response.responseCategoryList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("vendors")
    @Expose
    private List<Vendor> vendors = null;
    @SerializedName("your_location")
    @Expose
    private YourLocation yourLocation;

    public List<Vendor> getVendors() {
        return vendors;
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
    }

    public YourLocation getYourLocation() {
        return yourLocation;
    }

    public void setYourLocation(YourLocation yourLocation) {
        this.yourLocation = yourLocation;
    }

}
