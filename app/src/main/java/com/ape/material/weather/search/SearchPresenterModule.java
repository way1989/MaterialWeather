package com.ape.material.weather.search;

import com.ape.material.weather.util.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by android on 16-11-25.
 */
@Module
public class SearchPresenterModule {

    private final SearchContract.View mView;

    SearchPresenterModule(SearchContract.View view) {
        mView = view;
    }

    @Provides
    @ActivityScope
    SearchContract.View getView() {
        return mView;
    }

    @Provides
    @ActivityScope
    SearchContract.Model provideModel(SearchModel model) {
        return model;
    }
}
