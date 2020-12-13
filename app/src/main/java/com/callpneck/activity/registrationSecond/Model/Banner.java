package com.callpneck.activity.registrationSecond.Model;

public class Banner {

     private int image;
     String name, inStock;

    public Banner() {
    }

    public Banner(int image, String name, String inStock) {
        this.image = image;
        this.name = name;
        this.inStock = inStock;
    }

    public String getInStock() {
        return inStock;
    }

    public void setInStock(String inStock) {
        this.inStock = inStock;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
