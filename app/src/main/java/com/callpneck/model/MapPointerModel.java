package com.callpneck.model;

import java.io.Serializable;

public class MapPointerModel implements Serializable {
    private String shop_latitude;
    private String shop_longitude;
    private String shop_category;
    private String shop_image;
    private String shop_location;
    private String shop_name;
    private String shop_id;

    public MapPointerModel() {
    }

    public MapPointerModel(String shop_id) {
        this.shop_id = shop_id;
    }

    public MapPointerModel(String shop_latitude, String shop_longitude, String shop_category,
                           String shop_image, String shop_location, String shop_name, String shop_id) {
        this.shop_latitude = shop_latitude;
        this.shop_longitude = shop_longitude;
        this.shop_category = shop_category;
        this.shop_image = shop_image;
        this.shop_location = shop_location;
        this.shop_name = shop_name;
        this.shop_id = shop_id;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual=false;

        if (((MapPointerModel)obj).shop_id.equalsIgnoreCase(this.shop_id)){
            isEqual=true;
        }
        return isEqual;
    }

    public String getShop_latitude() {
        return shop_latitude;
    }

    public void setShop_latitude(String shop_latitude) {
        this.shop_latitude = shop_latitude;
    }

    public String getShop_longitude() {
        return shop_longitude;
    }

    public void setShop_longitude(String shop_longitude) {
        this.shop_longitude = shop_longitude;
    }

    public String getShop_category() {
        return shop_category;
    }

    public void setShop_category(String shop_category) {
        this.shop_category = shop_category;
    }

    public String getShop_image() {
        return shop_image;
    }

    public void setShop_image(String shop_image) {
        this.shop_image = shop_image;
    }

    public String getShop_location() {
        return shop_location;
    }

    public void setShop_location(String shop_location) {
        this.shop_location = shop_location;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }
    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }
}
