package com.unipi.talepis.fourthapp;

public class LocationDataClass {
    private String name;
    private String location_X;
    private String location_Y;


    public LocationDataClass(String name, String x, String y) {
        this.name = name;
        this.location_X = x;
        this.location_Y = y;
    }

    public String getName(){
        return this.name;
    }
    public String getLocation_X(){ return this.location_X; }
    public String getLocation_Y(){return this.location_Y;}
    public void setName(String n){
        this.name = n;
    }
    public void setLocation_X(String x){
        this.location_X = x;
    }
    public void setLocation_Y(String y){this.location_Y = y;}
}
