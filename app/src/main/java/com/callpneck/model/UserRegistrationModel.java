package com.callpneck.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserRegistrationModel implements Parcelable {
    public UserRegistrationModel() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUfname() {
        return ufname;
    }

    public void setUfname(String ufname) {
        this.ufname = ufname;
    }

    public String getUlname() {
        return ulname;
    }

    public void setUlname(String ulname) {
        this.ulname = ulname;
    }

    public String getUemail() {
        return uemail;
    }

    public void setUemail(String uemail) {
        this.uemail = uemail;
    }

    public String getUphone() {
        return uphone;
    }

    public void setUphone(String uphone) {
        this.uphone = uphone;
    }

    public String getUpass() {
        return upass;
    }

    public void setUpass(String upass) {
        this.upass = upass;
    }

    public String getUdeviceid() {
        return udeviceid;
    }

    public void setUdeviceid(String udeviceid) {
        this.udeviceid = udeviceid;
    }

    public static Creator<UserRegistrationModel> getCREATOR() {
        return CREATOR;
    }

    String message;
    String status;
    String ufname;
    String ulname;
    String uemail;
    String uphone;
    String upass;
    String udeviceid;

    protected UserRegistrationModel(Parcel in) {
        message = in.readString();
        status = in.readString();
        ufname = in.readString();
        ulname = in.readString();
        uemail = in.readString();
        uphone = in.readString();
        upass = in.readString();
        udeviceid = in.readString();
    }

    public static final Creator<UserRegistrationModel> CREATOR = new Creator<UserRegistrationModel>() {
        @Override
        public UserRegistrationModel createFromParcel(Parcel in) {
            return new UserRegistrationModel(in);
        }

        @Override
        public UserRegistrationModel[] newArray(int size) {
            return new UserRegistrationModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(status);
        dest.writeString(ufname);
        dest.writeString(ulname);
        dest.writeString(uemail);
        dest.writeString(uphone);
        dest.writeString(upass);
        dest.writeString(udeviceid);
    }
}
