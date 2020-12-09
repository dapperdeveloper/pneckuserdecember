package com.callpneck.activity.registrationSecond.Model;

public class ModelServices {

    String title, price;

    public ModelServices() {
    }

    public ModelServices(String title, String price) {
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
