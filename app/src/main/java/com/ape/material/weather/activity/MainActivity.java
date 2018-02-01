package com.ape.material.weather.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ape.material.weather.R;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.fragment.BaseFragment;
import com.ape.material.weather.fragment.WeatherFragment;
import com.ape.material.weather.util.RxSchedulers;
import com.ape.material.weather.util.UiUtil;
import com.ape.material.weather.widget.SimplePagerIndicator;
import com.ape.material.weather.widget.dynamic.BaseWeatherType;
import com.ape.material.weather.widget.dynamic.DynamicWeatherView;
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.functions.Consumer;


public class MainActivity extends BaseActivity
        implements WeatherFragment.OnDrawerTypeChangeListener {
    private static final String TAG = "MainActivity";
    public static final String UNKNOWN_CITY = "unknown";
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
    private List<City> mCities = new ArrayList<>();
    private int mSelectItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mMainViewPager.setPadding(0, UiUtil.getStatusBarHeight() + UiUtil.getActionBarHeight(), 0, 0);
        mAppBarLayout.setPadding(0, UiUtil.getStatusBarHeight(), 0, 0);
        ((ViewGroup) mIndicator.getParent()).setPadding(0, UiUtil.getStatusBarHeight(), 0, 0);

        setSupportActionBar(mToolbar);
        setupToolBar();
        setupViewPager();

        getCities();//加载城市列表
    }

    private void setupViewPager() {
        mAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
        mMainViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mMainViewPager);
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
                final BaseFragment fragment = mAdapter.getCurrentFragment();
                Log.d(TAG, "onMenuItemClick: id = share... currentFragment = " + fragment);
                if (fragment != null) {
                    fragment.onShareItemClick();
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
                getCities();
            } else if (mSelectItem >= 0 && mSelectItem < mAdapter.getCount()) {
                mMainViewPager.setCurrentItem(mSelectItem);
                mSelectItem = -1;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
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
        Log.d(TAG, "onCityChange: cities = " + cities);
        mCities = cities;
        if (mCities != null && !mCities.isEmpty()) {
            mAdapter.setNewData(mCities);
            mMainViewPager.setAdapter(mAdapter);
            if (mSelectItem >= 0 && mSelectItem < mAdapter.getCount()) {
                mMainViewPager.setCurrentItem(mSelectItem);
                mIndicator.notifyDataSetChanged();
                mSelectItem = -1;
            }
        }
    }

    private SpannableString getTitle(City city) {
        final String name = TextUtils.isEmpty(city.getCity()) ? UNKNOWN_CITY : city.getCity();
        if (city.getIsLocation() != 1) {
            return new SpannableString(name);
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

        SpannableString spannableString = new SpannableString(name + " ");
        spannableString.setSpan(drawableSpan, spannableString.length() - 1,
                spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public void getCities() {
        Log.d(TAG, "getCities: start.... mViewModel = " + mViewModel);
        mViewModel.getCities()
                .compose(RxSchedulers.<List<City>>io_main())
                .compose(this.<List<City>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<City>>() {
                    @Override
                    public void accept(List<City> cities) throws Exception {
                        Log.d(TAG, "getCities onNext: cities = " + cities.size());
                        onCityChange(cities);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "getCities onError: ", throwable);
                    }
                });
    }

    @Override
    public void onDrawerTypeChange(BaseWeatherType type) {
        mDynamicWeatherView.setType(type);
    }

    @Override
    public void onLocationChange(City city) {
        int index = -1;
        for (int i = 0; i < mCities.size(); i++) {
            if (mCities.get(i).getIsLocation() == 1) {
                index = i;
                break;
            }
        }
        Log.d(TAG, "onLocationChange: city = " + city + ", location city index = " + index);
        if (index >= 0 && index < mCities.size()) {
            mAdapter.replace(index, city.getCity());
            mIndicator.notifyDataSetChanged();
        }
    }

    public static class MainFragmentPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager mFragmentManager;
        private List<BaseFragment> mFragments;
        private List<String> mTitles;
        private BaseFragment mCurrentFragment;

        MainFragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            mFragmentManager = fragmentManager;
            this.mFragments = new ArrayList<>();
            this.mTitles = new ArrayList<>();
        }

        void setNewData(List<City> cities) {
            if (!mFragments.isEmpty()) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                for (Fragment f : mFragments) {
                    ft.remove(f);
                }
                ft.commitAllowingStateLoss();
                mFragmentManager.executePendingTransactions();
            }
            mFragments.clear();
            mTitles.clear();
            for (City city : cities) {
                Log.i(TAG, "city = " + city.getCity());
                mFragments.add(WeatherFragment.makeInstance(city));
                mTitles.add(TextUtils.isEmpty(city.getCity()) ? UNKNOWN_CITY : city.getCity());
            }
            notifyDataSetChanged();
        }

        @Override
        public BaseFragment getItem(int position) {
            BaseFragment fragment = mFragments.get(position);
            fragment.setRetainInstance(true);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            final CharSequence charSequence = mTitles.get(position);
            return TextUtils.isEmpty(charSequence) ? UNKNOWN_CITY : charSequence;
        }

        @Override
        public int getCount() {
            return mFragments.size();
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

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
//            return POSITION_NONE;
        }

        BaseFragment getCurrentFragment() {
            return mCurrentFragment;
        }

        void replace(int index, String city) {
            mTitles.remove(index);
            mTitles.add(index, city);
        }
    }
}
