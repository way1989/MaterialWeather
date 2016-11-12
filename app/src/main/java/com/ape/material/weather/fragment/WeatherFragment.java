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
import com.ape.material.weather.bean.HeWeather;
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
    public void onWeatherChange(HeWeather weather) {
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

        mWPullRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mWPullRefreshLayout.setRefreshing(true);
                getWeather(false);
            }
        });
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

    private void updateWeatherUI(HeWeather weather) {
        mWPullRefreshLayout.setRefreshing(false);
        if (weather == null || !weather.isOK()) {
            return;
        }
        try {
            mWeatherType = FormatUtil.convertWeatherType(weather);
            mListener.onDrawerTypeChange(mWeatherType);

            HeWeather.HeWeather5Bean w = weather.getWeather();
            mWDailyForecastView.setData(weather);
            mWHourlyForecastView.setData(weather);
            mWAqiView.setData(w.getAqi());
            mWAstroView.setData(weather);
            final String tmp = w.getNow().getTmp();
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

            setTextViewString(R.id.w_now_cond_text, w.getNow().getCond().getTxt());

            if (FormatUtil.isToday(w.getBasic().getUpdate().getLoc())) {
                setTextViewString(R.id.w_basic_update_loc, w.getBasic().getUpdate().getLoc().substring(11) + " 发布");
            } else {
                setTextViewString(R.id.w_basic_update_loc, w.getBasic().getUpdate().getLoc().substring(5) + " 发布");
            }

            setTextViewString(R.id.w_todaydetail_bottomline, w.getNow().getCond().getTxt() + "  " + weather.getTodayTempDescription());
            setTextViewString(R.id.w_todaydetail_temp, w.getNow().getTmp() + "°");

            setTextViewString(R.id.w_now_fl, w.getNow().getFl() + "°");
            setTextViewString(R.id.w_now_hum, w.getNow().getHum() + "%");// 湿度
            setTextViewString(R.id.w_now_vis, w.getNow().getVis() + "km");// 能见度

            setTextViewString(R.id.w_now_pcpn, w.getNow().getPcpn() + "mm"); // 降雨量

            if (weather.hasAqi()) {
                setTextViewString(R.id.w_aqi_text, w.getAqi().getCity().getQlty());
                setTextViewString(R.id.w_aqi_detail_text, w.getAqi().getCity().getQlty());
                setTextViewString(R.id.w_aqi_pm25, w.getAqi().getCity().getPm25() + "μg/m³");
                setTextViewString(R.id.w_aqi_pm10, w.getAqi().getCity().getPm10() + "μg/m³");
                setTextViewString(R.id.w_aqi_so2, w.getAqi().getCity().getSo2() + "μg/m³");
                setTextViewString(R.id.w_aqi_no2, w.getAqi().getCity().getNo2() + "μg/m³");
            } else {
                setTextViewString(R.id.w_aqi_text, "");
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
