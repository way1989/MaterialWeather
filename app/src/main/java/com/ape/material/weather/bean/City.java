package com.ape.material.weather.bean;

import com.ape.material.weather.manage.AbstractDataProvider;

import java.io.Serializable;

/**
 * Created by android on 16-11-10.
 */

public class City extends AbstractDataProvider.Data implements Serializable {
    private static final long serialVersionUID = -1233425412975945445L;
    private String city;
    private String cnty;
    private String areaId;
    private String lat;
    private String lon;
    private String prov;

    public City() {

    }

    public City(String city, String cnty, String id, String lat, String lon, String prov) {
        this.city = city;
        this.cnty = cnty;
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

    public String getCnty() {
        return cnty;
    }

    public void setCnty(String cnty) {
        this.cnty = cnty;
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

    @Override
    public long getId() {
        return areaId.hashCode();
    }

    @Override
    public boolean isSectionHeader() {
        return false;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public String getText() {
        return city;
    }

    @Override
    public boolean isPinned() {
        return false;
    }

    @Override
    public void setPinned(boolean pinned) {

    }
}
