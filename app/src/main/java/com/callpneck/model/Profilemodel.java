package com.callpneck.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Profilemodel implements Parcelable {


    public String getEmpFname() {
        return empFname;
    }

    public void setEmpFname(String empFname) {
        this.empFname = empFname;
    }

    public String getEmpLname() {
        return empLname;
    }

    public void setEmpLname(String empLname) {
        this.empLname = empLname;
    }

    public String getEmpVlno() {
        return empVlno;
    }

    public void setEmpVlno(String empVlno) {
        this.empVlno = empVlno;
    }

    public String getEmpGender() {
        return empGender;
    }

    public void setEmpGender(String empGender) {
        this.empGender = empGender;
    }

    public static Creator<Profilemodel> getCREATOR() {
        return CREATOR;
    }

    String empFname;
    String empLname;
    String empVlno;
    String empGender;

    public Profilemodel()
    {


    }

    protected Profilemodel(Parcel in) {
        empFname = in.readString();
        empLname = in.readString();
        empVlno = in.readString();
        empGender = in.readString();
    }

    public static final Creator<Profilemodel> CREATOR = new Creator<Profilemodel>() {
        @Override
        public Profilemodel createFromParcel(Parcel in) {
            return new Profilemodel(in);
        }

        @Override
        public Profilemodel[] newArray(int size) {
            return new Profilemodel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(empFname);
        dest.writeString(empLname);
        dest.writeString(empVlno);
        dest.writeString(empGender);
    }
}
