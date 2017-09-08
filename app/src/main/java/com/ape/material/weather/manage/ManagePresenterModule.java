package com.ape.material.weather.manage;

import com.ape.material.weather.util.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by android on 16-11-25.
 */
@Module
public class ManagePresenterModule {
    private ManageContract.View mView;

    ManagePresenterModule(ManageContract.View view) {
        mView = view;
    }

    @Provides
    @ActivityScope
    ManageContract.View getView() {
        return mView;
    }

    @Provides
    @ActivityScope
    ManageContract.Model provideModel(ManageModel model) {
        return model;
    }
}
