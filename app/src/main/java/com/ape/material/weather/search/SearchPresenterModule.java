package com.ape.material.weather.search;

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
    SearchContract.View getView() {
        return mView;
    }
}
