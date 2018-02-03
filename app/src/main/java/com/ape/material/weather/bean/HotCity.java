package com.ape.material.weather.bean;

import java.util.List;

/**
 * Created by android on 18-2-3.
 */

public class HotCity {
    private List<City> cities;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    @Override
    public String toString() {
        return "HotCity{" +
                "cities=" + cities +
                '}';
    }
}
