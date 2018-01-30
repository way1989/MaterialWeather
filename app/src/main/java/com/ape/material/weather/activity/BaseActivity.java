package com.ape.material.weather.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.ape.material.weather.App;
import com.ape.material.weather.dagger2.AppComponent;
import com.ape.material.weather.dagger2.DaggerWeatherComponent;
import com.ape.material.weather.dagger2.WeatherModule;
import com.ape.material.weather.dagger2.WeatherViewModel;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by android on 18-1-30.
 */

public abstract class BaseActivity extends RxAppCompatActivity {
    @Inject
    protected WeatherViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initPresenter(((App) getApplication()).getAppComponent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * should override this method when use MVP
     */
    protected void initPresenter(AppComponent appComponent) {
        DaggerWeatherComponent.builder().appComponent(appComponent)
                .weatherModule(new WeatherModule(this)).build().inject(this);
    }

    /**
     * must override this method
     *
     * @return resource layout id
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

}
