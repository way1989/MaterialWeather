package com.ape.material.weather.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ape.material.weather.R;
import com.ape.material.weather.base.BaseFragment;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.entity.HeWeather5;
import com.ape.material.weather.bean.entity.Weather;
import com.ape.material.weather.dynamicweather.BaseDrawer;
import com.ape.material.weather.util.FormatUtil;
import com.ape.material.weather.widget.AqiView;
import com.ape.material.weather.widget.AstroView;
import com.ape.material.weather.widget.DailyForecastView;
import com.ape.material.weather.widget.HourlyForecastView;
import com.ape.material.weather.widget.PullRefreshLayout;

import butterknife.BindView;

/**
 * Created by android on 16-11-10.
 */

public class WeatherFragment extends BaseFragment<WeatherPresenter, WeatherModel>
        implements WeatherContract.View, PullRefreshLayout.OnRefreshListener {
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
    private City mCity;
    private OnDrawerTypeChangeListener mListener;
    private BaseDrawer.Type mWeatherType;

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
    protected void initPresenter() {
        super.initPresenter();
        mPresenter.setVM(this, mModel);
    }

    private City getArgCity() {
        return (City) getArguments().getSerializable(ARG_KEY);
    }

    @Override
    public String getTitle() {
        return getArgCity().getCity();
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
    public void onWeatherChange(Weather weather) {
        updateWeatherUI(weather);
    }

    @Override
    public void showErrorTip(String msg) {
        mWPullRefreshLayout.setRefreshing(false);
        toast(msg);
    }

    @Override
    public void loadDataFirstTime() {
        if (mWPullRefreshLayout == null || getActivity() == null) return;

        mWPullRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWPullRefreshLayout.setRefreshing(true);
                getWeather(false);
            }
        }, 100);
    }

    private void getWeather(boolean force) {
        mPresenter.getWeather(mCity.getId(), "zh-cn", force);
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
        getWeather(true);
    }

    private void updateWeatherUI(Weather weather) {
        mWPullRefreshLayout.setRefreshing(false);
        if (weather == null || !weather.isOK()) {
            return;
        }
        try {
            mWeatherType = FormatUtil.convertWeatherType(weather);
            mListener.onDrawerTypeChange(mWeatherType);

            HeWeather5 w = weather.get();
            mWDailyForecastView.setData(weather);
            mWHourlyForecastView.setData(weather);
            mWAqiView.setData(w.aqi);
            mWAstroView.setData(weather);
            final String tmp = w.now.tmp;
            try {
                final int tmp_int = Integer.valueOf(tmp);
                if (tmp_int < 0) {
                    setTextViewString(R.id.w_now_tmp, String.valueOf(-tmp_int));
                    rootView.findViewById(R.id.w_now_tmp_minus).setVisibility(View.VISIBLE);
                } else {
                    setTextViewString(R.id.w_now_tmp, tmp);
                    rootView.findViewById(R.id.w_now_tmp_minus).setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                setTextViewString(R.id.w_now_tmp, tmp);
                rootView.findViewById(R.id.w_now_tmp_minus).setVisibility(View.GONE);
            }

            setTextViewString(R.id.w_now_cond_text, w.now.cond.txt);

            if (FormatUtil.isToday(w.basic.update.loc)) {
                setTextViewString(R.id.w_basic_update_loc, w.basic.update.loc.substring(11) + " 发布");
            } else {
                setTextViewString(R.id.w_basic_update_loc, w.basic.update.loc.substring(5) + " 发布");
            }

            setTextViewString(R.id.w_todaydetail_bottomline, w.now.cond.txt + "  " + weather.getTodayTempDescription());
            setTextViewString(R.id.w_todaydetail_temp, w.now.tmp + "°");

            setTextViewString(R.id.w_now_fl, w.now.fl + "°");
            setTextViewString(R.id.w_now_hum, w.now.hum + "%");// 湿度
            setTextViewString(R.id.w_now_vis, w.now.vis + "km");// 能见度

            setTextViewString(R.id.w_now_pcpn, w.now.pcpn + "mm"); // 降雨量

            if (weather.hasAqi()) {
                setTextViewString(R.id.w_aqi_text, w.aqi.city.qlty);
                setTextViewString(R.id.w_aqi_detail_text, w.aqi.city.qlty);
                setTextViewString(R.id.w_aqi_pm25, w.aqi.city.pm25 + "μg/m³");
                setTextViewString(R.id.w_aqi_pm10, w.aqi.city.pm10 + "μg/m³");
                setTextViewString(R.id.w_aqi_so2, w.aqi.city.so2 + "μg/m³");
                setTextViewString(R.id.w_aqi_no2, w.aqi.city.no2 + "μg/m³");
            } else {
                setTextViewString(R.id.w_aqi_text, "");
            }
            if (w.suggestion != null) {
                setTextViewString(R.id.w_suggestion_comf, w.suggestion.comf.txt);
                setTextViewString(R.id.w_suggestion_cw, w.suggestion.cw.txt);
                setTextViewString(R.id.w_suggestion_drsg, w.suggestion.drsg.txt);
                setTextViewString(R.id.w_suggestion_flu, w.suggestion.flu.txt);
                setTextViewString(R.id.w_suggestion_sport, w.suggestion.sport.txt);
                setTextViewString(R.id.w_suggestion_tarv, w.suggestion.trav.txt);
                setTextViewString(R.id.w_suggestion_uv, w.suggestion.uv.txt);
                setTextViewString(R.id.w_suggestion_comf_brf, w.suggestion.comf.brf);
                setTextViewString(R.id.w_suggestion_cw_brf, w.suggestion.cw.brf);
                setTextViewString(R.id.w_suggestion_drsg_brf, w.suggestion.drsg.brf);
                setTextViewString(R.id.w_suggestion_flu_brf, w.suggestion.flu.brf);
                setTextViewString(R.id.w_suggestion_sport_brf, w.suggestion.sport.brf);
                setTextViewString(R.id.w_suggestion_tarv_brf, w.suggestion.trav.brf);
                setTextViewString(R.id.w_suggestion_uv_brf, w.suggestion.uv.brf);
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
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    public interface OnDrawerTypeChangeListener {
        void onDrawerTypeChange(BaseDrawer.Type type);
    }

}
