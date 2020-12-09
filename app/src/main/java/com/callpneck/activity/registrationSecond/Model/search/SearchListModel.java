package com.callpneck.activity.registrationSecond.Model.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchListModel {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("service_id")
    @Expose
    private int service_id;

    @SerializedName("category_images")
    @Expose
    private String category_images;

    @SerializedName("title")
    @Expose
    private String title;


    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("body")
    @Expose
    private String body;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    public String getCategory_images() {
        return category_images;
    }

    public void setCategory_images(String category_images) {
        this.category_images = category_images;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
