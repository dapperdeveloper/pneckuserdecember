
package com.callpneck.activity.registrationSecond.Model.userList;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PneckUserList {

    @SerializedName("pneck_list")
    @Expose
    private List<PneckList> pneckList = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;

    public List<PneckList> getPneckList() {
        return pneckList;
    }

    public void setPneckList(List<PneckList> pneckList) {
        this.pneckList = pneckList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
