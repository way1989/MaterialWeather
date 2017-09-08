package com.ape.material.weather.main;

import com.ape.material.weather.util.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by android on 16-11-25.
 */
@Module
public final class MainPresenterModule {
    private MainContract.View mView;

    MainPresenterModule(MainContract.View view) {
        mView = view;
    }

    @Provides
    @ActivityScope
    MainContract.View getView() {
        return mView;
    }

    @Provides
    @ActivityScope
    MainContract.Model provideModel(MainModel model) {
        return model;
    }

}
