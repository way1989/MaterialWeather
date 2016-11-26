package com.ape.material.weather.main;

import android.content.Context;

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
    MainContract.Model getModel(Context context) {
        return new MainModel(context);
    }

    @Provides
    MainContract.View getView() {
        return mView;
    }
}
