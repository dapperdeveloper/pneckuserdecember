package com.callpneck.activity.registrationSecond.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {

    @SerializedName("id")
    @Expose
    Integer id;
    @SerializedName("user_id")
    @Expose
    Integer user_id;
    @SerializedName("fk_category_id")
    @Expose
    String fk_category_id;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("mrp")
    @Expose
    String mrp;

    @SerializedName("selling_price")
    @Expose
    String selling_price;

    @SerializedName("quantitytype")
    @Expose
    String quantitytype;

    @SerializedName("quantity")
    @Expose
    String quantity;

    @SerializedName("details")
    @Expose
    String details;

    @SerializedName("stock")
    @Expose
    String stock;

    @SerializedName("image")
    @Expose
    List<String> image;

    @SerializedName("categoryname")
    @Expose
    String categoryname;

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getFk_category_id() {
        return fk_category_id;
    }

    public void setFk_category_id(String fk_category_id) {
        this.fk_category_id = fk_category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(String selling_price) {
        this.selling_price = selling_price;
    }

    public String getQuantitytype() {
        return quantitytype;
    }

    public void setQuantitytype(String quantitytype) {
        this.quantitytype = quantitytype;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }
}
