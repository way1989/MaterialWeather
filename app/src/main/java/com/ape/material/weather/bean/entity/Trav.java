package com.ape.material.weather.bean.entity;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Trav implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3639679505779111846L;
    @SerializedName("brf")
    @Expose
    public String brf;
    @SerializedName("txt")
    @Expose
    public String txt;

}
