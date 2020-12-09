
package com.callpneck.activity.registrationSecond.Model.sendMoneyResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("add_latitude")
    @Expose
    private Object addLatitude;
    @SerializedName("add_longitude")
    @Expose
    private Object addLongitude;
    @SerializedName("usr_address")
    @Expose
    private Object usrAddress;
    @SerializedName("usr_gender")
    @Expose
    private Object usrGender;
    @SerializedName("first_name")
    @Expose
    private Object firstName;
    @SerializedName("last_name")
    @Expose
    private Object lastName;
    @SerializedName("device_id")
    @Expose
    private Object deviceId;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("email_verified_at")
    @Expose
    private Object emailVerifiedAt;
    @SerializedName("profile_image")
    @Expose
    private Object profileImage;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("mobile_verified_at")
    @Expose
    private String mobileVerifiedAt;
    @SerializedName("ep_token")
    @Expose
    private String epToken;
    @SerializedName("login_type")
    @Expose
    private String loginType;
    @SerializedName("social_id")
    @Expose
    private Object socialId;
    @SerializedName("is_active")
    @Expose
    private Integer isActive;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("state_id")
    @Expose
    private Integer stateId;
    @SerializedName("status")
    @Expose
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getAddLatitude() {
        return addLatitude;
    }

    public void setAddLatitude(Object addLatitude) {
        this.addLatitude = addLatitude;
    }

    public Object getAddLongitude() {
        return addLongitude;
    }

    public void setAddLongitude(Object addLongitude) {
        this.addLongitude = addLongitude;
    }

    public Object getUsrAddress() {
        return usrAddress;
    }

    public void setUsrAddress(Object usrAddress) {
        this.usrAddress = usrAddress;
    }

    public Object getUsrGender() {
        return usrGender;
    }

    public void setUsrGender(Object usrGender) {
        this.usrGender = usrGender;
    }

    public Object getFirstName() {
        return firstName;
    }

    public void setFirstName(Object firstName) {
        this.firstName = firstName;
    }

    public Object getLastName() {
        return lastName;
    }

    public void setLastName(Object lastName) {
        this.lastName = lastName;
    }

    public Object getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Object deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(Object emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public Object getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Object profileImage) {
        this.profileImage = profileImage;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobileVerifiedAt() {
        return mobileVerifiedAt;
    }

    public void setMobileVerifiedAt(String mobileVerifiedAt) {
        this.mobileVerifiedAt = mobileVerifiedAt;
    }

    public String getEpToken() {
        return epToken;
    }

    public void setEpToken(String epToken) {
        this.epToken = epToken;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public Object getSocialId() {
        return socialId;
    }

    public void setSocialId(Object socialId) {
        this.socialId = socialId;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
