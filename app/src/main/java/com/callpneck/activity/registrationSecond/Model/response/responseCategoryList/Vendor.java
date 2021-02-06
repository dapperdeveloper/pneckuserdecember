
package com.callpneck.activity.registrationSecond.Model.response.responseCategoryList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vendor {

    @SerializedName("vendor_id")
    @Expose
    private String vendorId;
    @SerializedName("first_name")
    @Expose
    private Object firstName;
    @SerializedName("shop_title")
    @Expose
    private String shopTitle;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("opening_time")
    @Expose
    private String openingTime;
    @SerializedName("closing_time")
    @Expose
    private String closingTime;
    @SerializedName("phone")
    @Expose
    private Object phone;
    @SerializedName("image")
    @Expose
    private Object image;
    @SerializedName("professional_service")
    @Expose
    private String professionalService;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("cate_type")
    @Expose
    private String cateType;
    @SerializedName("catalogues")
    @Expose
    private String catalogues;
    @SerializedName("days")
    @Expose
    private String days;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("distance_km")
    @Expose
    private String distanceKm;
    @SerializedName("curr_latitude")
    @Expose
    private String currLatitude;
    @SerializedName("curr_longitude")
    @Expose
    private String currLongitude;
    @SerializedName("curr_loc_address")
    @Expose
    private Object currLocAddress;

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public Object getFirstName() {
        return firstName;
    }

    public void setFirstName(Object firstName) {
        this.firstName = firstName;
    }

    public String getShopTitle() {
        return shopTitle;
    }

    public void setShopTitle(String shopTitle) {
        this.shopTitle = shopTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public Object getImage() {
        return image;
    }

    public void setImage(Object image) {
        this.image = image;
    }

    public String getProfessionalService() {
        return professionalService;
    }

    public void setProfessionalService(String professionalService) {
        this.professionalService = professionalService;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCateType() {
        return cateType;
    }

    public void setCateType(String cateType) {
        this.cateType = cateType;
    }

    public String getCatalogues() {
        return catalogues;
    }

    public void setCatalogues(String catalogues) {
        this.catalogues = catalogues;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(String distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getCurrLatitude() {
        return currLatitude;
    }

    public void setCurrLatitude(String currLatitude) {
        this.currLatitude = currLatitude;
    }

    public String getCurrLongitude() {
        return currLongitude;
    }

    public void setCurrLongitude(String currLongitude) {
        this.currLongitude = currLongitude;
    }

    public Object getCurrLocAddress() {
        return currLocAddress;
    }

    public void setCurrLocAddress(Object currLocAddress) {
        this.currLocAddress = currLocAddress;
    }

}
