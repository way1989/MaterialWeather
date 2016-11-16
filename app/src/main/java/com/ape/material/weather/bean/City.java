package com.ape.material.weather.bean;

import java.io.Serializable;

/**
 * Created by android on 16-11-10.
 */

public class City implements Serializable {
    private static final long serialVersionUID = -1233425412975945445L;
    private String city;
    private String country;
    private String areaId;
    private String lat;
    private String lon;
    private String prov;
    private boolean isLocation;
    private int index;

    public City() {

    }

    public City(String city, String country, String id, String lat, String lon, String prov) {
        this.city = city;
        this.country = country;
        this.areaId = id;
        this.lat = lat;
        this.lon = lon;
        this.prov = prov;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String id) {
        this.areaId = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public void setLocation(boolean location) {
        isLocation = location;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "City{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", areaId='" + areaId + '\'' +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", prov='" + prov + '\'' +
                ", isLocation=" + isLocation +
                ", index=" + index +
                '}';
    }

}
