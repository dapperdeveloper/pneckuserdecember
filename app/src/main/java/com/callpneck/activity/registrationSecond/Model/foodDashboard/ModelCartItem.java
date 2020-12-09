package com.callpneck.activity.registrationSecond.Model.foodDashboard;

public class ModelCartItem {

    String id, pId, name,image, price, cost, quantity;

    public ModelCartItem() {
    }

    public ModelCartItem(String id, String pId, String name, String image, String price, String cost, String quantity) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.image = image;
        this.price = price;
        this.cost = cost;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
