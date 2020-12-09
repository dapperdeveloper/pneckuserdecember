package com.callpneck.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NearByEmployeeModel implements Parcelable {

    String first_name;
    String is_online;
    String duty_status;
    String distance_km;
    String c_latitute;
    String c_longitute;
    String c_address;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getIs_online() {
        return is_online;
    }

    public void setIs_online(String is_online) {
        this.is_online = is_online;
    }

    public String getDuty_status() {
        return duty_status;
    }

    public void setDuty_status(String duty_status) {
        this.duty_status = duty_status;
    }

    public String getDistance_km() {
        return distance_km;
    }

    public void setDistance_km(String distance_km) {
        this.distance_km = distance_km;
    }

    public String getC_latitute() {
        return c_latitute;
    }

    public void setC_latitute(String c_latitute) {
        this.c_latitute = c_latitute;
    }

    public String getC_longitute() {
        return c_longitute;
    }

    public void setC_longitute(String c_longitute) {
        this.c_longitute = c_longitute;
    }

    public String getC_address() {
        return c_address;
    }

    public void setC_address(String c_address) {
        this.c_address = c_address;
    }

    public static Creator<NearByEmployeeModel> getCREATOR() {
        return CREATOR;
    }

    public NearByEmployeeModel()
    {

    }

    protected NearByEmployeeModel(Parcel in) {
        first_name = in.readString();
        is_online = in.readString();
        duty_status = in.readString();
        distance_km = in.readString();
        c_latitute = in.readString();
        c_longitute = in.readString();
        c_address = in.readString();
    }

    public static final Creator<NearByEmployeeModel> CREATOR = new Creator<NearByEmployeeModel>() {
        @Override
        public NearByEmployeeModel createFromParcel(Parcel in) {
            return new NearByEmployeeModel(in);
        }

        @Override
        public NearByEmployeeModel[] newArray(int size) {
            return new NearByEmployeeModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(first_name);
        parcel.writeString(is_online);
        parcel.writeString(duty_status);
        parcel.writeString(distance_km);
        parcel.writeString(c_latitute);
        parcel.writeString(c_longitute);
        parcel.writeString(c_address);
    }
}
