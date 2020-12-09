package com.callpneck.taxi.model;

public class CarType {
    int id;
    String carName;
    String imageUrl;

    public CarType(int id, String carName, String imageUrl) {
        this.id = id;
        this.carName = carName;
        this.imageUrl = imageUrl;
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
}
