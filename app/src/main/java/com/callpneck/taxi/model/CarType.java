package com.callpneck.taxi.model;

public class CarType {
    int id;
    String carName;
    String imageUrl;

    int selected;

    public CarType(int id, String carName, String imageUrl,int selected) {
        this.id = id;
        this.carName = carName;
        this.imageUrl = imageUrl;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

}
