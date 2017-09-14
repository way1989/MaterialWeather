package com.ape.material.weather.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ape.material.weather.AppComponent;
import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.R;
import com.ape.material.weather.base.BaseFragment;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.dynamicweather.BaseDrawer;
import com.ape.material.weather.util.DeviceUtil;
import com.ape.material.weather.util.FormatUtil;
import com.ape.material.weather.widget.AqiView;
import com.ape.material.weather.widget.AstroView;
import com.ape.material.weather.widget.DailyForecastView;
import com.ape.material.weather.widget.HourlyForecastView;
import com.ape.material.weather.widget.PullRefreshLayout;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by android on 16-11-10.
 */

@RuntimePermissions
public class WeatherFragment extends BaseFragment<WeatherPresenter>
        implements WeatherContract.View, PullRefreshLayout.OnRefreshListener {
    private static final String TAG = "WeatherFragment";
    private static final String ARG_KEY = "city";
    @BindView(R.id.w_dailyForecastView)
    DailyForecastView mWDailyForecastView;
    @BindView(R.id.w_hourlyForecastView)
    HourlyForecastView mWHourlyForecastView;
    @BindView(R.id.w_aqi_view)
    AqiView mWAqiView;
    @BindView(R.id.w_astroView)
    AstroView mWAstroView;
    @BindView(R.id.w_WeatherScrollView)
    ScrollView mWWeatherScrollView;
    @BindView(R.id.w_PullRefreshLayout)
    PullRefreshLayout mWPullRefreshLayout;
    @BindView(R.id.city_title_tv)
    TextView mCityTitleTv;

    private OnDrawerTypeChangeListener mListener;
    private BaseDrawer.Type mWeatherType;
    private City mCity;
    private HeWeather mWeather;

    public static WeatherFragment makeInstance(@NonNull City city) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_KEY, city);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDrawerTypeChangeListener) {
            mListener = (OnDrawerTypeChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDrawerTypeChangeListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (mWeather == null || !DeviceUtil.hasInternet(getContext()))
//            return;
//        mPresenter.getWeather(mCity.getAreaId(), true);
    }

    @Override
    protected void initPresenter(AppComponent appComponent) {
        super.initPresenter(appComponent);
        DaggerWeatherComponent.builder().appComponent(appComponent).weatherPresenterModule(new WeatherPresenterModule(this)).build().inject(this);
    }

    private City getArgCity() {
        return (City) getArguments().getSerializable(ARG_KEY);
    }

    @Override
    public BaseDrawer.Type getDrawerType() {
        return mWeatherType;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_weather;
    }

    @Override
    public void onWeatherChange(HeWeather weather) {
        mWeather = weather;
        updateWeatherUI();
    }

    @Override
    public void onCityChange(City city) {
        mCity = city;
        setTitle();
        if (getUserVisibleHint()) mListener.onCityChange(city);
        getWeather(city, false);
    }

    @Override
    public void showErrorTip(String msg) {
        mWPullRefreshLayout.setRefreshing(false);
        if (BuildConfig.LOG_DEBUG)
            toast(msg);
    }

    @Override
    public void loadDataFirstTime() {
        if (mWPullRefreshLayout == null || getActivity() == null) return;

        mWPullRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mWPullRefreshLayout.setRefreshing(true);
                getWeather(mCity, false);
            }
        });
    }

    private void getWeather(City city, boolean force) {
        Log.i(TAG, "getWeather... city = " + city + ", areaId = " + city.getAreaId()
                + ", request location = "
                + (city.isLocation() && TextUtils.isEmpty(city.getAreaId())));
        if (city.isLocation() && TextUtils.isEmpty(city.getAreaId())) {
            if (!DeviceUtil.hasInternet(getContext())) {
                showErrorTip(getString(R.string.no_internet_toast));
                return;
            }
            if (DeviceUtil.isGPSProviderEnabled(getContext())) {
                WeatherFragmentPermissionsDispatcher.getLocationWithCheck(this);
            } else {
                showErrorTip(getString(R.string.gps_disabled_toast));
            }
            return;
        }

        mPresenter.getWeather(city.getAreaId(), force);
    }

    @Override
    protected void initView() {
        mCity = getArgCity();
        setTitle();
        mWPullRefreshLayout.setOnRefreshListener(this);

        if (mWWeatherScrollView != null)
            mWWeatherScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mWWeatherScrollView.scrollTo(0, 0);
                }
            });
    }

    private void setTitle() {
//        if (!mCity.isLocation())
//            mCityTitleTv.setCompoundDrawables(null, null, null, null);
//        mCityTitleTv.setText(TextUtils.isEmpty(mCity.getCity()) ? getString(R.string.auto_location) : mCity.getCity());
    }

    @Override
    public void onRefresh() {
        getWeather(mCity, true);
    }

    private void updateWeatherUI() {
        final HeWeather weather = mWeather;
        mWPullRefreshLayout.setRefreshing(false);
        if (weather == null || !weather.isOK()) {
            return;
        }
        try {
            mWeatherType = FormatUtil.convertWeatherType(weather);
            if (getUserVisibleHint()) mListener.onDrawerTypeChange(mWeatherType);

            HeWeather.HeWeather5Bean w = weather.getWeather();
            mWDailyForecastView.setData(weather);
            mWHourlyForecastView.setData(weather);
            mWAstroView.setData(weather);
            setTextViewString(R.id.w_now_tmp, getString(R.string.weather_temp, w.getNow().getTmp()));

            if (FormatUtil.isToday(w.getBasic().getUpdate().getLoc())) {
                setTextViewString(R.id.w_basic_update_loc,
                        getString(R.string.weather_update, w.getBasic().getUpdate().getLoc().substring(11)));
            } else {
                setTextViewString(R.id.w_basic_update_loc,
                        getString(R.string.weather_update, w.getBasic().getUpdate().getLoc().substring(5)));
            }

            setTextViewString(R.id.w_todaydetail_bottomline, w.getNow().getCond().getTxt() + "  " + weather.getTodayTempDescription());
            setTextViewString(R.id.w_todaydetail_temp, getString(R.string.weather_temp, w.getNow().getTmp()));

            setTextViewString(R.id.w_now_fl, getString(R.string.weather_temp, w.getNow().getFl()));
            setTextViewString(R.id.w_now_hum, getString(R.string.weather_percent, w.getNow().getHum()));// 湿度
            setTextViewString(R.id.w_now_vis, getString(R.string.weather_km, w.getNow().getVis()));// 能见度
            setTextViewString(R.id.w_now_pcpn, getString(R.string.weather_mm, w.getNow().getPcpn())); // 降雨量

            if (weather.hasAqi()) {
                mWAqiView.setData(w.getAqi());
                final String qlty = w.getAqi().getCity().getQlty();
                if (TextUtils.isEmpty(qlty)) {
                    setTextViewString(R.id.w_now_cond_text, w.getNow().getCond().getTxt());
                } else {
                    setTextViewString(R.id.w_now_cond_text, w.getNow().getCond().getTxt() + " | " + qlty);
                }
                setTextViewString(R.id.w_aqi_detail_text, qlty);
                final String pm25 = w.getAqi().getCity().getPm25();
                setTextViewString(R.id.w_aqi_pm25, TextUtils.isEmpty(pm25) ? getString(R.string.nodata)
                        : getString(R.string.weather_ug_m3, pm25));
                final String pm10 = w.getAqi().getCity().getPm10();
                setTextViewString(R.id.w_aqi_pm10, TextUtils.isEmpty(pm10) ? getString(R.string.nodata)
                        : getString(R.string.weather_ug_m3, pm10));
                final String so2 = w.getAqi().getCity().getSo2();
                setTextViewString(R.id.w_aqi_so2, TextUtils.isEmpty(so2) ? getString(R.string.nodata)
                        : getString(R.string.weather_ug_m3, so2));
                final String no2 = w.getAqi().getCity().getNo2();
                setTextViewString(R.id.w_aqi_no2, TextUtils.isEmpty(no2) ? getString(R.string.nodata)
                        : getString(R.string.weather_ug_m3, no2));
            } else {
                setTextViewString(R.id.w_now_cond_text, w.getNow().getCond().getTxt());
                rootView.findViewById(R.id.air_quality_item).setVisibility(View.GONE);
            }
            if (w.getSuggestion() != null) {
                setTextViewString(R.id.w_suggestion_comf, w.getSuggestion().getComf().getTxt());
                setTextViewString(R.id.w_suggestion_cw, w.getSuggestion().getCw().getTxt());
                setTextViewString(R.id.w_suggestion_drsg, w.getSuggestion().getDrsg().getTxt());
                setTextViewString(R.id.w_suggestion_flu, w.getSuggestion().getFlu().getTxt());
                setTextViewString(R.id.w_suggestion_sport, w.getSuggestion().getSport().getTxt());
                setTextViewString(R.id.w_suggestion_tarv, w.getSuggestion().getTrav().getTxt());
                setTextViewString(R.id.w_suggestion_uv, w.getSuggestion().getUv().getTxt());
                setTextViewString(R.id.w_suggestion_comf_brf, w.getSuggestion().getComf().getBrf());
                setTextViewString(R.id.w_suggestion_cw_brf, w.getSuggestion().getCw().getBrf());
                setTextViewString(R.id.w_suggestion_drsg_brf, w.getSuggestion().getDrsg().getBrf());
                setTextViewString(R.id.w_suggestion_flu_brf, w.getSuggestion().getFlu().getBrf());
                setTextViewString(R.id.w_suggestion_sport_brf, w.getSuggestion().getSport().getBrf());
                setTextViewString(R.id.w_suggestion_tarv_brf, w.getSuggestion().getTrav().getBrf());
                setTextViewString(R.id.w_suggestion_uv_brf, w.getSuggestion().getUv().getBrf());
            } else {
                rootView.findViewById(R.id.index_item).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            toast(mCity.getCity() + " Error\n" + e.toString());
        }
    }

    private void setTextViewString(int textViewId, String str) {

        TextView tv = (TextView) rootView.findViewById(textViewId);
        if (tv != null) {
            tv.setText(str);
        } else {
            toast("Error NOT found textView id->" + Integer.toHexString(textViewId));
        }
    }

    protected void toast(String msg) {
        Snackbar.make(mWPullRefreshLayout, msg, Snackbar.LENGTH_SHORT).show();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getLocation() {
        mPresenter.getLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WeatherFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void onShowRational(final PermissionRequest request) {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.permission_title)
                .setMessage(R.string.permission_message).setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.cancel();
            }
        }).show();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void onPermissionDenied() {
        Snackbar.make(mWWeatherScrollView, R.string.permission_denied, Snackbar.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void onNeverAskAgain() {
        Snackbar.make(mWWeatherScrollView, R.string.permission_never_ask_again, Snackbar.LENGTH_SHORT).show();
    }

    public interface OnDrawerTypeChangeListener {
        void onDrawerTypeChange(BaseDrawer.Type type);

        void onCityChange(City city);
    }

}
