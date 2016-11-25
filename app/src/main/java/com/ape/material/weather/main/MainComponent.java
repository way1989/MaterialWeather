package com.ape.material.weather.main;

import com.ape.material.weather.AppComponent;
import com.ape.material.weather.util.FragmentScoped;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by android on 16-11-25.
 */
@FragmentScoped
@Component(dependencies = AppComponent.class, modules = MainPresenterModule.class)
public interface MainComponent {
    void inject(MainActivity activity);
}
