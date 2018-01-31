package com.ape.material.weather.bean;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;


/**
 * Created by android on 16-11-10.
 */
@Entity(tableName = "city")
public class City implements Serializable {
    private static final long serialVersionUID = -1233425412975945445L;
    @PrimaryKey(autoGenerate = true)
    private long _id;
    private String city;
    private String country;
    private String areaId;
    @ColumnInfo(name = "latitude")
    private String lat;
    @ColumnInfo(name = "longitude")
    private String lon;
    @ColumnInfo(name = "province")
    private String prov;
    private int isLocation;
    @ColumnInfo(name = "orderIndex")
    private int index;

    public City() {

    }

    @Ignore
    public City(String city, String country, String id, String lat, String lon, String prov) {
        this.city = city;
        this.country = country;
        this.areaId = id;
        this.lat = lat;
        this.lon = lon;
        this.prov = prov;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
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

    public int getIsLocation() {
        return isLocation;
    }

    public void setIsLocation(int isLocation) {
        this.isLocation = isLocation;
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
