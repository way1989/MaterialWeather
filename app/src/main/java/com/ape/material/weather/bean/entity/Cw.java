package com.ape.material.weather.bean.entity;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Cw implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5891282439346796286L;
    @SerializedName("brf")
    @Expose
    public String brf;
    @SerializedName("txt")
    @Expose
    public String txt;

}
