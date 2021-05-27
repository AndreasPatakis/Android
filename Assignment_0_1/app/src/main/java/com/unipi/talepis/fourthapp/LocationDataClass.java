package com.unipi.talepis.fourthapp;

public class LocationDataClass {
    private String name;
    private String location;
    private String key;

    public LocationDataClass(String key, String name, String location) {
        this.name = name;
        this.location = location;
        this.key = key;
    }

    public String getName(){
        return this.name;
    }
    public String getLocation(){
        return this.location;
    }
    public String getKey(){return this.key;}
    public void setName(String n){
        this.name = n;
    }
    public void setLocation(String l){
        this.location = l;
    }
    public void setKey(String k){this.key = k;}
}
