package com.ape.material.weather.fragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by android on 16-11-25.
 */
@Module
public class WeatherPresenterModule {
    private WeatherContract.View mView;

    WeatherPresenterModule(WeatherContract.View view) {
        mView = view;
    }

    @Provides
    WeatherContract.Model getModel() {
        return new WeatherModel();
    }

    @Provides
    WeatherContract.View getView() {
        return mView;
    }
}
