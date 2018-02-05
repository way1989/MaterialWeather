package com.ape.material.weather;

import android.app.Application;

import com.ape.material.weather.dagger2.AppComponent;
import com.ape.material.weather.dagger2.AppModule;
import com.ape.material.weather.dagger2.DaggerAppComponent;
import com.squareup.leakcanary.LeakCanary;

import io.github.skyhacker2.sqliteonweb.SQLiteOnWeb;


/**
 * Created by way on 16/6/10.
 */
public class App extends Application {
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);
        SQLiteOnWeb.init(this).start();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
