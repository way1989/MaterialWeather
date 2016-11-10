package com.ape.material.weather.bean.entity;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Drsg implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8057157411680581237L;
    @SerializedName("brf")
    @Expose
    public String brf;
    @SerializedName("txt")
    @Expose
    public String txt;

}
