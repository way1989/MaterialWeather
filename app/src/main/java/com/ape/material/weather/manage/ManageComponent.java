package com.ape.material.weather.manage;

import com.ape.material.weather.AppComponent;
import com.ape.material.weather.util.FragmentScoped;

import dagger.Component;

/**
 * Created by android on 16-11-25.
 */
@FragmentScoped
@Component(dependencies = AppComponent.class, modules = ManagePresenterModule.class)
public interface ManageComponent {
    void inject(ManageActivity activity);
}
