package com.ape.material.weather.dagger2;

import com.ape.material.weather.activity.BaseActivity;
import com.ape.material.weather.fragment.BaseFragment;
import com.ape.material.weather.util.ActivityScope;

import dagger.Component;

/**
 * Created by android on 16-11-25.
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = WeatherModule.class)
public interface WeatherComponent {
    void inject(BaseActivity activity);

    void inject(BaseFragment fragment);
}
