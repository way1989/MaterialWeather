package com.ape.material.weather.search;

import com.ape.material.weather.AppComponent;
import com.ape.material.weather.util.FragmentScoped;

import dagger.Component;

/**
 * Created by android on 16-11-25.
 */
@FragmentScoped
@Component(dependencies = AppComponent.class, modules = SearchPresenterModule.class)
public interface SearchComponent {
    void inject(SearchCityActivity activity);
}
