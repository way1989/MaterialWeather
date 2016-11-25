package com.ape.material.weather.fragment;

import com.ape.material.weather.AppComponent;
import com.ape.material.weather.util.FragmentScoped;

import dagger.Component;

/**
 * Created by android on 16-11-25.
 */
@FragmentScoped
@Component(dependencies = AppComponent.class, modules = WeatherPresenterModule.class)
public interface WeatherComponent {
    void inject(WeatherFragment fragment);
}
