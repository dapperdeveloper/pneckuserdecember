package com.callpneck.activity.registrationSecond.DemoModel;

import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CatResModel {
    private String category_merchant_id;
    private String category_name;
    public String getId_kategori_merchant() {
        return category_merchant_id;
    }

    public CatResModel(String category_name) {
        this.category_name = category_name;
    }

    public void setId_kategori_merchant(String category_merchant_id) {
        this.category_merchant_id = category_merchant_id;
    }
    public String getNama_kategori() {
        return category_name;
    }

    public void setNama_kategori(String category_name) {
        this.category_name = category_name;
    }

}
