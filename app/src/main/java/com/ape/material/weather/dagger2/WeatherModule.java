package com.ape.material.weather.dagger2;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.ape.material.weather.data.IRepositoryManager;
import com.ape.material.weather.util.ActivityScope;

import java.lang.reflect.InvocationTargetException;

import dagger.Module;
import dagger.Provides;

/**
 * Created by android on 16-11-25.
 */
@Module
public final class WeatherModule {
    private AppCompatActivity mActivity;

    public WeatherModule(AppCompatActivity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityScope
    WeatherViewModel provideWeatherViewModel(final AppCompatActivity activity, final Application application, final IRepositoryManager userRepository) {
        return ViewModelProviders.of(activity, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                try {
                    return modelClass.getConstructor(Application.class, IRepositoryManager.class).newInstance(application, userRepository);
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                }
            }
        }).get(WeatherViewModel.class);
    }

    @Provides
    @ActivityScope
    AppCompatActivity provideActivity() {
        return mActivity;
    }

}
