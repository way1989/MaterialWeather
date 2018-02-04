package com.ape.material.weather.bean;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * Created by way on 2018/2/4.
 */

public class SearchItem extends SectionEntity<City> {
    public SearchItem(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public SearchItem(City city) {
        super(city);
    }
}
