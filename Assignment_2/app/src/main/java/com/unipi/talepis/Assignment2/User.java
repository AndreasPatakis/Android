package com.unipi.talepis.Assignment2;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String uID;
    private String firstname;
    private String lastname;
    private String address;

    private String region;

    public User(){

    }

    public User(String uID, String firstname, String lastname, String address,String region) {
        this.uID = uID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.region = region;
    }


    protected User(Parcel in) {
        uID = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        address = in.readString();
        region = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegion() { return region; }

    public void setRegion(String region) { this.region = region; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uID);
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeString(address);
        dest.writeString(region);
    }
}
