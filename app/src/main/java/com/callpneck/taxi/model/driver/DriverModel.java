
package com.callpneck.taxi.model.driver;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverModel {

    @SerializedName("response")
    @Expose
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}
