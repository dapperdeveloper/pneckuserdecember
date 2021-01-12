
package com.callpneck.activity.registrationSecond.Model.BookingResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("my_orders")
    @Expose
    private List<MyOrder> myOrders = null;

    public List<MyOrder> getMyOrders() {
        return myOrders;
    }

    public void setMyOrders(List<MyOrder> myOrders) {
        this.myOrders = myOrders;
    }

}
