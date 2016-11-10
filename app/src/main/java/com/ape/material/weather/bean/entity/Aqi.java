package com.ape.material.weather.bean.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Aqi implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4426260758809374490L;
    @SerializedName("city")
    @Expose
    public City city;

}
