package com.ape.material.weather;

import android.content.Context;

import com.ape.material.weather.data.WeatherRepository;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by android on 16-11-25.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    Context getContext();
    WeatherRepository getWeatherRepository();
}
