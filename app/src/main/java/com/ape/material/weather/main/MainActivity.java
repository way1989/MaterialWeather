package com.ape.material.weather.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ape.material.weather.R;
import com.ape.material.weather.base.BaseActivity;
import com.ape.material.weather.base.BaseFragment;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.dynamicweather.BaseDrawer;
import com.ape.material.weather.dynamicweather.DynamicWeatherView;
import com.ape.material.weather.fragment.WeatherFragment;
import com.ape.material.weather.manage.ManageLocationActivity;
import com.ape.material.weather.util.UiUtil;
import com.ape.material.weather.widget.MxxFragmentPagerAdapter;
import com.ape.material.weather.widget.MxxViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

import static com.ape.material.weather.R.id.toolbar;

public class MainActivity extends BaseActivity<MainPresenter, MainModel>
        implements MainContract.View, WeatherFragment.OnDrawerTypeChangeListener {
    private static final String TAG = "MainActivity";
    @BindView(R.id.dynamic_weather_view)
    DynamicWeatherView mDynamicWeatherView;
    @BindView(R.id.main_view_pager)
    MxxViewPager mMainViewPager;
    @BindView(toolbar)
    Toolbar mToolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    private MainFragmentPagerAdapter mAdapter;

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

        mPresenter.getCities();//加载城市列表
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
                startActivity(new Intent(this, ManageLocationActivity.class));
                return true;
            case R.id.action_share:
                return true;
            case R.id.action_settings:
                return true;
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
        return R.layout.app_bar_main;
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        mPresenter.setVM(this, mModel);
    }

    @Override
    public void onCityChange(List<City> cities) {
        if (cities != null && !cities.isEmpty()) {

            List<BaseFragment> weatherFragmentList = new ArrayList<>();
            for (City city : cities) {
                Log.i(TAG, "city = " + city.getCity());
                weatherFragmentList.add(WeatherFragment.makeInstance(city));

            }
            if (mAdapter == null) {
                mAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), weatherFragmentList);
            } else {
                //刷新fragment
                mAdapter.setFragments(getSupportFragmentManager(), weatherFragmentList);
            }
            mMainViewPager.setAdapter(mAdapter);
            //mMainViewPager.setOffscreenPageLimit(weatherFragmentList.size() - 1);
            mMainViewPager.setOnPageChangeListener(new MxxViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    mDynamicWeatherView.setDrawerType(mAdapter.getItem(position).getDrawerType());
                }
            });
        }
    }

    @Override
    public void onDrawerTypeChange(BaseDrawer.Type type) {
        mDynamicWeatherView.setDrawerType(type);
    }

    public static class MainFragmentPagerAdapter extends MxxFragmentPagerAdapter {

        private List<BaseFragment> mFragmentList;

        MainFragmentPagerAdapter(FragmentManager fragmentManager, List<BaseFragment> fragmentList) {
            super(fragmentManager);
            setFragments(fragmentManager, fragmentList);
        }

        //刷新fragment
        void setFragments(FragmentManager fm, List<BaseFragment> fragments) {
            if (this.mFragmentList != null) {
                FragmentTransaction ft = fm.beginTransaction();
                for (Fragment f : this.mFragmentList) {
                    ft.remove(f);
                }
                ft.commitAllowingStateLoss();
                fm.executePendingTransactions();
            }
            this.mFragmentList = fragments;
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
            return mFragmentList.get(position).getTitle();
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

    }
}
