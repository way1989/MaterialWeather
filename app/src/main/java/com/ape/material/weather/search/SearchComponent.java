package com.ape.material.weather.search;

import com.ape.material.weather.AppComponent;
import com.ape.material.weather.util.ActivityScope;

import dagger.Component;

/**
 * Created by android on 16-11-25.
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = SearchPresenterModule.class)
public interface SearchComponent {
    void inject(SearchCityActivity activity);
}
