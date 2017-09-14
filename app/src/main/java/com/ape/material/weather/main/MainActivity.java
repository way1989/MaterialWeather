package com.ape.material.weather.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ape.material.weather.AppComponent;
import com.ape.material.weather.R;
import com.ape.material.weather.base.BaseActivity;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.dynamicweather.BaseDrawer;
import com.ape.material.weather.dynamicweather.DynamicWeatherView;
import com.ape.material.weather.fragment.WeatherFragment;
import com.ape.material.weather.manage.ManageActivity;
import com.ape.material.weather.util.RxBus;
import com.ape.material.weather.util.RxEvent;
import com.ape.material.weather.util.UiUtil;
import com.lwj.widget.viewpagerindicator.ViewPagerIndicator;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import static com.ape.material.weather.R.id.toolbar;

public class MainActivity extends BaseActivity<MainPresenter>
        implements MainContract.View, WeatherFragment.OnDrawerTypeChangeListener {
    private static final String TAG = "MainActivity";
    @BindView(R.id.dynamic_weather_view)
    DynamicWeatherView mDynamicWeatherView;
    @BindView(R.id.main_view_pager)
    ViewPager mMainViewPager;
    @BindView(toolbar)
    Toolbar mToolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.indicator_spring)
    ViewPagerIndicator mIndicator;
    private FragmentPagerAdapter mAdapter;
    private WeatherFragment mCurFragment;
    private List<City> mCities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mMainViewPager.setPadding(0, UiUtil.getStatusBarHeight(), 0, 0);
            mAppBarLayout.setPadding(0, UiUtil.getStatusBarHeight(), 0, 0);
        }
        final int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        BaseDrawer.Type type;
        if (hourOfDay >= 7 && hourOfDay <= 18) {
            type = BaseDrawer.Type.UNKNOWN_D;
        } else {
            type = BaseDrawer.Type.UNKNOWN_N;
        }
        mDynamicWeatherView.setDrawerType(type);
        setSupportActionBar(mToolbar);
        setTitle("");

        reloadCity();//加载城市列表
        RxBus.getInstance().toObservable(RxEvent.MainEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<RxEvent.MainEvent>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<RxEvent.MainEvent>() {
                    @Override
                    public void accept(RxEvent.MainEvent mainEvent) throws Exception {
                        if (mainEvent.position >= 0 && mainEvent.position < mAdapter.getCount()) {
                            mMainViewPager.setCurrentItem(mainEvent.position);
                            setTitle(getTitle(mCities.get(mainEvent.position)));
                            return;
                        }
                        List<City> cities = mainEvent.cities;
                        if (cities != null) {
                            onCityChange(cities);
                        } else {
                            reloadCity();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }

                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_manage:
                startActivity(new Intent(this, ManageActivity.class));
                return true;
          /*  case R.id.action_share:
                return true;
            case R.id.action_settings:
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mDynamicWeatherView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDynamicWeatherView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDynamicWeatherView.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initPresenter(AppComponent appComponent) {
        super.initPresenter(appComponent);
        DaggerMainComponent.builder().appComponent(appComponent)
                .mainPresenterModule(new MainPresenterModule(this)).build().inject(this);
    }

    @Override
    public void onCityChange(List<City> cities) {
        if (cities != null && !cities.isEmpty()) {
            mCities = cities;
            if (mAdapter == null) {
                mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

                    @Override
                    public int getCount() {
                        return mCities.size();
                    }

                    @Override
                    public Fragment getItem(int position) {
                        return WeatherFragment.makeInstance(mCities.get(position));
                    }

                    @Override
                    public void setPrimaryItem(ViewGroup container, int position, Object object) {
                        super.setPrimaryItem(container, position, object);
                        mCurFragment = (WeatherFragment) object;
                    }

                    //this is called when notifyDataSetChanged() is called
                    @Override
                    public int getItemPosition(Object object) {
                        return PagerAdapter.POSITION_NONE;
                    }
                };
            } else {
                //刷新fragment
                mAdapter.notifyDataSetChanged();
            }
            mMainViewPager.setAdapter(mAdapter);
            mMainViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    mDynamicWeatherView.setDrawerType(mCurFragment.getDrawerType());
                    setTitle(getTitle(mCities.get(position)));
                }
            });
            mIndicator.setViewPager(mMainViewPager);
            mIndicator.setVisibility(mCities.size() > 1 ? View.VISIBLE : View.GONE);
            setTitle(getTitle(mCities.get(0)));
        }
    }

    private SpannableString getTitle(City city) {
        if (!city.isLocation()) {
            return new SpannableString(TextUtils.isEmpty(city.getCity()) ? "unknown" : city.getCity());
        }
        ImageSpan imgSpan = new ImageSpan(getApplicationContext(), R.drawable.ic_location_on_white_18dp);
        SpannableString spannableString = new SpannableString(TextUtils.isEmpty(city.getCity())
                ? getString(R.string.auto_location) + " " : city.getCity() + " ");
        spannableString.setSpan(imgSpan, spannableString.length() - 1,
                spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @Override
    public void reloadCity() {
        mPresenter.getCities();
    }

    @Override
    public void onDrawerTypeChange(BaseDrawer.Type type) {
        mDynamicWeatherView.setDrawerType(type);
    }

    @Override
    public void onCityChange(City city) {
        setTitle(getTitle(city));
    }
}
