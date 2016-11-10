package com.ape.material.weather.bean.entity;

import android.text.TextUtils;

import com.ape.material.weather.util.FormatUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Weather implements Serializable {

    private static final long serialVersionUID = -821374811106598097L;

    @SerializedName("HeWeather5")
    @Expose
    public List<HeWeather5> HeWeatherDataService30 = new ArrayList<>(1);

    public static String prettyUpdateTime(HeWeather5 w) {
        try {
            if (FormatUtil.isToday(w.basic.update.loc)) {
                return w.basic.update.loc.substring(11) + " 发布";
            } else {
                return w.basic.update.loc.substring(5) + " 发布";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "? 发布";
    }

    public boolean isOK() {
        if (this.HeWeatherDataService30.size() > 0) {
            final HeWeather5 dataService30 = this.HeWeatherDataService30.get(0);
            return TextUtils.equals(dataService30.status, "ok");
        }
        return false;
    }

    public boolean hasAqi() {
        if (isOK()) {
            final HeWeather5 h = get();
            return h.aqi != null && h.aqi.city != null;
        }
        return true;
    }

    public HeWeather5 get() {
        // if(this.HeWeather5.size() > 0){
        return this.HeWeatherDataService30.get(0);
        // }
        // return null;
    }

    /**
     * 出错返回-1
     *
     * @returnTodayDailyForecastIndex
     */
    public int getTodayDailyForecastIndex() {
        int todayIndex = -1;
        if (!isOK()) {
            return todayIndex;
        }
        final HeWeather5 w = get();
        for (int i = 0; i < w.dailyForecast.size(); i++) {
            if (FormatUtil.isToday(w.dailyForecast.get(i).date)) {
                todayIndex = i;
                break;
            }
        }
        return todayIndex;
    }

    /**
     * 出错返回null
     *
     * @return Today DailyForecast
     */
    public DailyForecast getTodayDailyForecast() {
        final int todayIndex = getTodayDailyForecastIndex();
        if (todayIndex != -1) {
            DailyForecast forecast = get().dailyForecast.get(todayIndex);
            return forecast;
        }
        return null;
    }

    /**
     * 今天的气温 6~16°
     *
     * @return
     */
    public String getTodayTempDescription() {
        final int todayIndex = getTodayDailyForecastIndex();
        if (todayIndex != -1) {
            DailyForecast forecast = get().dailyForecast.get(todayIndex);
            return forecast.tmp.min + "~" + forecast.tmp.max + "°";
        }
        return "";
    }
}
