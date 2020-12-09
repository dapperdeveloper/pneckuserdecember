package com.callpneck.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BikeLocation implements Parcelable {
    double lat;
    double longi;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public static Creator<BikeLocation> getCREATOR() {
        return CREATOR;
    }

    protected BikeLocation(Parcel in) {
        lat = in.readDouble();
        longi = in.readDouble();
    }

    public static final Creator<BikeLocation> CREATOR = new Creator<BikeLocation>() {
        @Override
        public BikeLocation createFromParcel(Parcel in) {
            return new BikeLocation(in);
        }

        @Override
        public BikeLocation[] newArray(int size) {
            return new BikeLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(longi);
    }
}
