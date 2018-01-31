package com.ape.material.weather.fragment;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.R;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.util.DeviceUtil;
import com.ape.material.weather.util.FormatUtil;
import com.ape.material.weather.util.RxImage;
import com.ape.material.weather.util.RxSchedulers;
import com.ape.material.weather.util.ShareUtils;
import com.ape.material.weather.widget.AqiView;
import com.ape.material.weather.widget.AstroView;
import com.ape.material.weather.widget.DailyForecastView;
import com.ape.material.weather.widget.HourlyForecastView;
import com.ape.material.weather.widget.dynamic.BaseWeatherType;
import com.ape.material.weather.widget.dynamic.ShortWeatherInfo;
import com.ape.material.weather.widget.dynamic.TypeUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.io.File;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by android on 16-11-10.
 */

public class WeatherFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
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
    SwipeRefreshLayout mWPullRefreshLayout;

    private OnDrawerTypeChangeListener mListener;
    private BaseWeatherType mWeatherType;
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

    private City getArgCity() {
        return (City) getArguments().getSerializable(ARG_KEY);
    }

    @Override
    public BaseWeatherType getDrawerType() {
        if (mWeather != null && mWeather.isOK()) {
            mWeatherType = TypeUtil.getType(getResources(), getShortWeatherInfo(mWeather));
        }
        return mWeatherType;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_weather;
    }

    @Override
    public void onShareItemClick() {
        if (getUserVisibleHint() && mWWeatherScrollView != null) {
            mWWeatherScrollView.scrollTo(0, 0);
            RxImage.saveText2ImageObservable(mWWeatherScrollView)
                    .compose(RxSchedulers.<File>io_main())
                    .compose(RxSchedulers.<File>io_main())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) throws Exception {
                            Log.d(TAG, "onShareItemClick onNext: file = " + file);
                            ShareUtils.shareImage(getActivity(), file, "分享到");
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e(TAG, "onShareItemClick onError: ", throwable);
                            showErrorTip("share error:" + throwable.getMessage());
                        }
                    });
        }
    }

    @Override
    public HeWeather getWeather() {
        return mWeather;
    }

    public void onWeatherChange(HeWeather weather) {
        mWeather = weather;
        updateWeatherUI();
    }

    public void onLocationChange(City city) {
        if (getUserVisibleHint()) {
            if (!TextUtils.equals(mCity.getAreaId(), city.getAreaId())
                    || !TextUtils.equals(mCity.getCity(), city.getCity())) {
                mListener.onLocationChange(city);
            }
            mCity = city;
            getWeather(city, true);
        }
    }

    public void showErrorTip(String msg) {
        mWPullRefreshLayout.setRefreshing(false);
        if (BuildConfig.LOG_DEBUG)
            toast(msg);
    }

    @Override
    public void loadDataFirstTime() {
        if (getActivity() == null || mCity == null) {
            showErrorTip("city is null");
            Log.e(TAG, "loadDataFirstTime: ", new Throwable("something is null..."));
            return;
        }
        if (mCity.getIsLocation() == 1 && TextUtils.isEmpty(mCity.getAreaId())) {
            getLocation();
        } else {
            getWeather(mCity, false);
        }
    }

    private void getWeather(City city, boolean force) {
        Log.i(TAG, "getWeather... city = " + city + ", areaId = " + city.getAreaId()
                + ", request location = " + city.getIsLocation());
        mViewModel.getWeather(city.getAreaId(), force)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mWPullRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mWPullRefreshLayout.setRefreshing(true);
                            }
                        });
                    }
                })
                .compose(this.<HeWeather>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .compose(RxSchedulers.<HeWeather>io_main())
                .subscribe(new Consumer<HeWeather>() {
                    @Override
                    public void accept(HeWeather heWeather) throws Exception {
                        onWeatherChange(heWeather);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        showErrorTip(throwable.getMessage());
                    }
                });

    }

    @Override
    protected void initView() {
        mCity = getArgCity();
        mWPullRefreshLayout.setOnRefreshListener(this);

        if (mWWeatherScrollView != null)
            mWWeatherScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mWWeatherScrollView.scrollTo(0, 0);
                }
            });
    }

    @Override
    public void onRefresh() {
        if (mCity.getIsLocation() == 1 && TextUtils.isEmpty(mCity.getAreaId())) {
            getLocation();
        } else {
            getWeather(mCity, true);
        }
    }

    private void updateWeatherUI() {
        final HeWeather weather = mWeather;
        mWPullRefreshLayout.setRefreshing(false);
        if (weather == null || !weather.isOK()) {
            return;
        }
        try {
            ShortWeatherInfo info = getShortWeatherInfo(weather);
            mWeatherType = TypeUtil.getType(getResources(), info);
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
            setTextViewString(R.id.w_now_hum, w.getNow().getHum() + "%");// 湿度
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
                mRootView.findViewById(R.id.air_quality_item).setVisibility(View.GONE);
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
                mRootView.findViewById(R.id.index_item).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            toast(mCity.getCity() + " Error\n" + e.toString());
        }
    }

    @NonNull
    private ShortWeatherInfo getShortWeatherInfo(HeWeather weather) {
        ShortWeatherInfo info = new ShortWeatherInfo();
        info.setCode(weather.getWeather().getNow().getCond().getCode());
        info.setWindSpeed(weather.getWeather().getNow().getWind().getSpd());
        info.setSunrise(weather.getWeather().getDaily_forecast().get(0).getAstro().getSr());
        info.setSunset(weather.getWeather().getDaily_forecast().get(0).getAstro().getSs());
        info.setMoonrise(weather.getWeather().getDaily_forecast().get(0).getAstro().getMr());
        info.setMoonset(weather.getWeather().getDaily_forecast().get(0).getAstro().getMs());
        return info;
    }

    private void setTextViewString(int textViewId, String str) {

        TextView tv = mRootView.findViewById(textViewId);
        if (tv != null) {
            tv.setText(str);
        } else {
            toast("Error NOT found textView id->" + Integer.toHexString(textViewId));
        }
    }

    protected void toast(String msg) {
        Snackbar.make(mWPullRefreshLayout, msg, Snackbar.LENGTH_SHORT).show();
    }

    private void getLocation() {
        Log.d(TAG, "getLocation: start...");
        new RxPermissions(getActivity())
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            requestLocation();
                        } else {
                            onNeverAskAgain();
                        }
                    }
                });
    }

    private void requestLocation() {
        Log.d(TAG, "requestLocation: mViewModel = " + mViewModel);
        mViewModel.getLocation()
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mWPullRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mWPullRefreshLayout.setRefreshing(true);
                            }
                        });
                    }
                })
                .compose(WeatherFragment.this.<City>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .compose(RxSchedulers.<City>io_main())
                .subscribe(new Consumer<City>() {
                    @Override
                    public void accept(City city) throws Exception {
                        Log.d(TAG, "accept: getLocation city = " + city);
                        onLocationChange(city);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        showErrorTip(throwable.getMessage());
                    }
                });
    }

    void onNeverAskAgain() {
        Snackbar.make(mWWeatherScrollView, R.string.permission_never_ask_again, Snackbar.LENGTH_SHORT).show();
    }

    public interface OnDrawerTypeChangeListener {
        void onDrawerTypeChange(BaseWeatherType type);

        void onLocationChange(City city);
    }

}
