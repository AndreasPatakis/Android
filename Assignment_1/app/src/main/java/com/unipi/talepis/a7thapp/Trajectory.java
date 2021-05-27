package com.unipi.talepis.a7thapp;

public class Trajectory {
    private String username;
    private int timestamp;
    private String longitude;
    private String latitude;
    private float speed;


    public Trajectory(){}

    public Trajectory(String username, int timestamp,String longitude,String latitude,float speed){
        this.username = username;
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }



}
