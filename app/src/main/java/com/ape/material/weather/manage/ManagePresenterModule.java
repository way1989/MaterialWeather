package com.ape.material.weather.manage;

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
    ManageContract.View getView() {
        return mView;
    }
}
