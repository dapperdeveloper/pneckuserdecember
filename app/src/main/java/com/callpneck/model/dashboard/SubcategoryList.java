
package com.callpneck.model.dashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubcategoryList {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("category_images")
    @Expose
    private Object categoryImages;

    @SerializedName("cate_type")
    @Expose
    private String cate_type;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getCategoryImages() {
        return categoryImages;
    }

    public void setCategoryImages(Object categoryImages) {
        this.categoryImages = categoryImages;
    }

    public String getCate_type() {
        return cate_type;
    }

    public void setCate_type(String cate_type) {
        this.cate_type = cate_type;
    }
}
