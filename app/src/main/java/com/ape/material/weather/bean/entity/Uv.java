package com.ape.material.weather.bean.entity;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Uv implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8823866836818234770L;
    @SerializedName("brf")
    @Expose
    public String brf;
    @SerializedName("txt")
    @Expose
    public String txt;

}
