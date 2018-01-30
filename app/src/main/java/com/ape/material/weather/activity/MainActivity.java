package com.ape.material.weather.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ape.material.weather.R;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.dynamicweather.BaseDrawer;
import com.ape.material.weather.dynamicweather.DynamicWeatherView;
import com.ape.material.weather.fragment.BaseFragment;
import com.ape.material.weather.fragment.WeatherFragment;
import com.ape.material.weather.share.ShareActivity;
import com.ape.material.weather.util.RxSchedulers;
import com.ape.material.weather.util.UiUtil;
import com.ape.material.weather.util.WeatherUtil;
import com.ape.material.weather.widget.SimplePagerIndicator;
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.functions.Consumer;


public class MainActivity extends BaseActivity
        implements WeatherFragment.OnDrawerTypeChangeListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_CITY = 0;
    @BindView(R.id.dynamic_weather_view)
    DynamicWeatherView mDynamicWeatherView;
    @BindView(R.id.main_view_pager)
    ViewPager mMainViewPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.indicator_spring)
    SimplePagerIndicator mIndicator;
    private MainFragmentPagerAdapter mAdapter;
    private BaseFragment mCurrentFragment;
    private List<String> mCities = new ArrayList<>();
    private int mSelectItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mMainViewPager.setPadding(0, UiUtil.getStatusBarHeight() + UiUtil.getActionBarHeight(), 0, 0);
        mAppBarLayout.setPadding(0, UiUtil.getStatusBarHeight(), 0, 0);
        ((ViewGroup) mIndicator.getParent()).setPadding(0, UiUtil.getStatusBarHeight(), 0, 0);

        final int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        BaseDrawer.Type type;
        if (hourOfDay >= 7 && hourOfDay <= 18) {
            type = BaseDrawer.Type.UNKNOWN_D;
        } else {
            type = BaseDrawer.Type.UNKNOWN_N;
        }
        mDynamicWeatherView.setDrawerType(type);
        setSupportActionBar(mToolbar);
        setupToolBar();

        reloadCity();//加载城市列表
    }

    private void setupToolBar() {
        setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_location_city);
        RxToolbar.navigationClicks(mToolbar)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        startActivityForResult(new Intent(MainActivity.this, ManageActivity.class), REQUEST_CODE_CITY);
                    }
                });
        RxToolbar.itemClicks(mToolbar)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<MenuItem>() {
                    @Override
                    public void accept(MenuItem menuItem) throws Exception {
                        onMenuItemClick(menuItem);
                    }
                });
    }

    private void onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_share:
                final HeWeather weather = mAdapter.getCurrentFragment().getWeather();
                if (weather != null) {
                    ShareActivity.start(MainActivity.this, WeatherUtil.getInstance().getShareMessage(weather));
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CITY && resultCode == RESULT_OK) {
            final boolean changed = data.getBooleanExtra(ManageActivity.EXTRA_DATA_CHANGED, false);
            mSelectItem = data.getIntExtra(ManageActivity.EXTRA_SELECTED_ITEM, -1);
            if (changed) {
                reloadCity();
            } else if (mSelectItem >= 0 && mSelectItem < mAdapter.getCount()) {
                mMainViewPager.setCurrentItem(mSelectItem);
                mSelectItem = -1;
            }
        }
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

    public void onCityChange(List<City> cities) {
        if (cities != null && !cities.isEmpty()) {
            List<BaseFragment> weatherFragmentList = new ArrayList<>();
            mCities.clear();
            for (City city : cities) {
                Log.i(TAG, "city = " + city.getCity());
                weatherFragmentList.add(WeatherFragment.makeInstance(city));
                mCities.add(TextUtils.isEmpty(city.getCity()) ? "unknown" : city.getCity());
            }
            if (mAdapter == null) {
                mAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), weatherFragmentList, mCities);
            } else {
                //刷新fragment
                mAdapter.setFragments(getSupportFragmentManager(), weatherFragmentList, mCities);
            }
            mMainViewPager.setAdapter(mAdapter);
            mMainViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    mDynamicWeatherView.setDrawerType(mAdapter.getItem(position).getDrawerType());
                }
            });
            mIndicator.setViewPager(mMainViewPager);
            mIndicator.notifyDataSetChanged();
            if (mSelectItem >= 0 && mSelectItem < mAdapter.getCount()) {
                mMainViewPager.setCurrentItem(mSelectItem);
                mSelectItem = -1;
            }
        }
    }

    private SpannableString getTitle(City city) {
        if (!city.isLocation()) {
            return new SpannableString(TextUtils.isEmpty(city.getCity()) ? "unknown" : city.getCity());
        }
        DynamicDrawableSpan drawableSpan =
                new DynamicDrawableSpan(DynamicDrawableSpan.ALIGN_BASELINE) {//基于文本基线,默认是文本底部
                    @Override
                    public Drawable getDrawable() {
                        Drawable d = getResources().getDrawable(R.drawable.ic_location_on_white_18dp);
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                        return d;
                    }
                };
        //ImageSpan imgSpan = new ImageSpan(getApplicationContext(), R.drawable.ic_location_on_white_18dp);

        SpannableString spannableString = new SpannableString(TextUtils.isEmpty(city.getCity())
                ? getString(R.string.auto_location) + " " : city.getCity() + " ");
        spannableString.setSpan(drawableSpan, spannableString.length() - 1,
                spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public void reloadCity() {
        mViewModel.getCities()
                .compose(RxSchedulers.<List<City>>io_main())
                .compose(this.<List<City>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<City>>() {
                    @Override
                    public void accept(List<City> cities) throws Exception {
                        Log.d(TAG, "accept: cities = " + cities.size());
                        onCityChange(cities);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "accept: throwable = " + throwable);
                    }
                });
    }

    @Override
    public void onDrawerTypeChange(BaseDrawer.Type type) {
        mDynamicWeatherView.setDrawerType(type);
    }

    @Override
    public void onCityChange(City city) {
        reloadCity();
    }

    public static class MainFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<BaseFragment> mFragmentList;
        private List<String> mTitleList;
        private BaseFragment mCurrentFragment;

        MainFragmentPagerAdapter(FragmentManager fragmentManager, List<BaseFragment> fragmentList, List<String> titleList) {
            super(fragmentManager);
            setFragments(fragmentManager, fragmentList, titleList);
        }

        //刷新fragment
        void setFragments(FragmentManager fm, List<BaseFragment> fragments, List<String> titleList) {
            if (this.mFragmentList != null) {
                FragmentTransaction ft = fm.beginTransaction();
                for (Fragment f : this.mFragmentList) {
                    ft.remove(f);
                }
                ft.commitAllowingStateLoss();
                fm.executePendingTransactions();
            }
            this.mFragmentList = fragments;
            this.mTitleList = titleList;
            notifyDataSetChanged();
        }

        @Override
        public BaseFragment getItem(int position) {
            BaseFragment fragment = mFragmentList.get(position);
            fragment.setRetainInstance(true);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(((Fragment) object).getView());
            super.destroyItem(container, position, object);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentFragment = (BaseFragment) object;
        }

        public BaseFragment getCurrentFragment() {
            return mCurrentFragment;
        }

    }
}
