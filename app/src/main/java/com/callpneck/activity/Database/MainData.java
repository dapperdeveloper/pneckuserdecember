package com.callpneck.activity.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "table_name")
public class MainData implements Serializable {

    //Create id column
    @PrimaryKey(autoGenerate = true)
    private int ID;

    //Create text column
    @ColumnInfo(name = "name")
    private String name;

    //Create image column
    @ColumnInfo(name = "image")
    private String image;

    //Create price column
    @ColumnInfo(name = "price")
    private String price;

    //Create cost column
    @ColumnInfo(name = "cost")
    private String cost;

    //Create quantity column
    @ColumnInfo(name = "quantity")
    private String quantity;

    public MainData() {
    }

    public MainData(String name, String image, String price, String cost, String quantity) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.cost = cost;
        this.quantity = quantity;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    @Override
    public String toString() {
        return "MainData{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", price='" + price + '\'' +
                ", cost='" + cost + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
