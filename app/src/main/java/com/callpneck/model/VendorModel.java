package com.callpneck.model;

public class VendorModel {

    private String vendorId,vendorRating,shopName,userName,userType,openTime,closeTime,userPhone,userImage,primeService,category,catalogue;
    private String distanc_km,current_latitude,current_longitude,current_address;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public VendorModel(String vendorId, String vendorRating, String shopName, String userName, String userType, String openTime, String closeTime, String userPhone,
                       String userImage, String primeService, String category, String catalogue,
                       String distanc_km, String current_latitude, String current_longitude, String current_address) {

        this.shopName=shopName;
        this.vendorId=vendorId;
        this.vendorRating=vendorRating;
        this.userName = userName;
        this.userType = userType;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.userPhone = userPhone;
        this.userImage = userImage;
        this.primeService = primeService;
        this.category = category;
        this.catalogue = catalogue;
        this.distanc_km = distanc_km;
        this.current_latitude = current_latitude;
        this.current_longitude = current_longitude;
        this.current_address = current_address;
    }

    public String getVendorRating() {
        return vendorRating;
    }

    public void setVendorRating(String vendorRating) {
        this.vendorRating = vendorRating;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getPrimeService() {
        return primeService;
    }

    public void setPrimeService(String primeService) {
        this.primeService = primeService;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(String catalogue) {
        this.catalogue = catalogue;
    }

    public String getDistanc_km() {
        return distanc_km;
    }

    public void setDistanc_km(String distanc_km) {
        this.distanc_km = distanc_km;
    }

    public String getCurrent_latitude() {
        return current_latitude;
    }

    public void setCurrent_latitude(String current_latitude) {
        this.current_latitude = current_latitude;
    }

    public String getCurrent_longitude() {
        return current_longitude;
    }

    public void setCurrent_longitude(String current_longitude) {
        this.current_longitude = current_longitude;
    }

    public String getCurrent_address() {
        return current_address;
    }

    public void setCurrent_address(String current_address) {
        this.current_address = current_address;
    }
}
