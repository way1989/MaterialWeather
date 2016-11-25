package com.ape.material.weather.manage;

import android.content.Context;

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
    ManageContract.Model getModel(Context context) {
        return new ManageModel(context);
    }

    @Provides
    ManageContract.View getView() {
        return mView;
    }
}
