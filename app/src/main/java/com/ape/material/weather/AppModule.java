package com.ape.material.weather;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by android on 16-11-25.
 */
@Module
public final class AppModule {
    private final Context mContext;

    AppModule(Context context) {
        mContext = context;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }
}
