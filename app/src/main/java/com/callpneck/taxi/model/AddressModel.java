package com.callpneck.taxi.model;

public class AddressModel {


    String Addtrss;
    Double latitude;
    Double longitude;

    public AddressModel(String addtrss, Double latitude, Double longitude) {
        Addtrss = addtrss;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddtrss() {
        return Addtrss;
    }

    public void setAddtrss(String addtrss) {
        Addtrss = addtrss;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
