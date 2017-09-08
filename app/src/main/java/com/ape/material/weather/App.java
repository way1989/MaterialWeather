package com.ape.material.weather;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.squareup.leakcanary.LeakCanary;

import io.github.skyhacker2.sqliteonweb.SQLiteOnWeb;


/**
 * Created by way on 16/6/10.
 */
public class App extends Application {
    private volatile static Context sContext;
    private volatile static Typeface sTypeface;
    private AppComponent mAppComponent;

    public static Context getContext() {
        return sContext;
    }

    public static Typeface getTypeface() {
        return sTypeface;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        if (sTypeface == null) {
            sTypeface = Typeface.createFromAsset(getAssets(), "fonts/weather_font.ttf");
        }

        LeakCanary.install(this);

        SQLiteOnWeb.init(this).start();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getApplicationContext())).build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
