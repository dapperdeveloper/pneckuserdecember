
package com.callpneck.activity.registrationSecond.Model.response.responseCategoryList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelProvider {

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
