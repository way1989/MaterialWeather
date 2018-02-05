package com.ape.material.weather.widget.dynamic;

import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.text.TextUtils;

import com.ape.material.weather.R;
import com.ape.material.weather.bean.HeWeather;

import java.util.Calendar;

/**
 * Created by liyu on 2017/10/12.
 */

public class TypeUtil {

    public static BaseWeatherType getType(Resources context, ShortWeatherInfo info) {
        return getHeWeatherType(context, info);
    }

    public static @ColorInt int getWeatherColor(HeWeather weather) {
        String weatherCode = weather.getWeather().getNow().getCond().getCode();
        final int w = Integer.valueOf(TextUtils.isEmpty(weatherCode) ? "999" : weatherCode);
        final int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final boolean isNotNight = hourOfDay >= 7 && hourOfDay <= 18;
        switch (w) {
            case 900:
            case 100:

            case 101:// 多云
            case 102:// 少云
            case 103:// 晴间多云
                return isNotNight ? 0xFF51C0F8 : 0xFF7F9EE9;
            case 104:// 阴
                return 0xFF6D8DB1;
            // 200 - 213是风
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case 207:
            case 208:
            case 209:
            case 210:
            case 211:
            case 212:
            case 213:
                return isNotNight ? 0xFF51C0F8 : 0xFF7F9EE9;
            case 300:// 阵雨Shower Rain
            case 305:// 小雨 Light Rain
            case 308:// 极端降雨 Extreme Rain
            case 309:// 毛毛雨/细雨 Drizzle Rain
            case 301:// 强阵雨 Heavy Shower Rain
            case 302:// 雷阵雨 Thundershower
            case 303:// 强雷阵雨 Heavy Thunderstorm
            case 306:// 中雨 Moderate Rain
            case 307:// 大雨 Heavy Rain
            case 310:// 暴雨 Storm
            case 311:// 大暴雨 Heavy Storm
            case 312:// 特大暴雨 Severe Storm
            case 404:// 雨夹雪 Sleet
            case 405:// 雨雪天气 Rain And Snow
            case 406:// 阵雨夹雪 Shower Snow
                return 0xFF6188DA;
            case 304:// 雷阵雨伴有冰雹 Hail
            case 313:// 冻雨 Freezing Rain
                return 0xFF0CB399;
            case 400:// 小雪 Light Snow
            case 401:// 中雪 Moderate Snow
            case 407:// 阵雪 Snow Flurry
            case 402:// 大雪 Heavy Snow
            case 403:// 暴雪 Snowstorm

                return 0xFF62B1FF;
            case 500:// 薄雾
            case 501:// 雾
                return 0xFF8CADD3;
            case 502:// 霾
            case 504:// 浮尘
                return 0xFF7F8195;
            case 503:// 扬沙
            case 506:// 火山灰
            case 507:// 沙尘暴
            case 508:// 强沙尘暴
                return 0xFFE99E3C;
            default:
                return R.drawable.ic_stat_icon_na;
        }
    }

    private static BaseWeatherType getHeWeatherType(Resources context, ShortWeatherInfo info) {
        if (info != null && TextUtils.isDigitsOnly(info.getCode())) {
            int code = Integer.parseInt(info.getCode());
            if (code == 100) {//晴
                return new SunnyType(context, info);
            } else if (code >= 101 && code <= 103) {//多云
                SunnyType sunnyType = new SunnyType(context, info);
                sunnyType.setCloud(true);
                return sunnyType;
            } else if (code == 104) {//阴
                return new OvercastType(context, info);
            } else if (code >= 200 && code <= 213) {//各种风
                return new SunnyType(context, info);
            } else if (code >= 300 && code <= 303) {//各种阵雨
                if (code >= 300 && code <= 301) {
                    return new RainType(context, RainType.RAIN_LEVEL_2, RainType.WIND_LEVEL_2);
                } else {
                    RainType rainType = new RainType(context, RainType.RAIN_LEVEL_2, RainType.WIND_LEVEL_2);
                    rainType.setFlashing(true);
                    return rainType;
                }
            } else if (code == 304) {//阵雨加冰雹
                return new HailType(context);
            } else if (code >= 305 && code <= 312) {//各种雨
                if (code == 305 || code == 309) {//小雨
                    return new RainType(context, RainType.RAIN_LEVEL_1, RainType.WIND_LEVEL_1);
                } else if (code == 306) {//中雨
                    return new RainType(context, RainType.RAIN_LEVEL_2, RainType.WIND_LEVEL_2);
                } else//大到暴雨
                    return new RainType(context, RainType.RAIN_LEVEL_3, RainType.WIND_LEVEL_3);
            } else if (code == 313) {//冻雨
                return new HailType(context);
            } else if (code >= 400 && code <= 407) {//各种雪
                if (code == 400) {
                    return new SnowType(context, SnowType.SNOW_LEVEL_1);
                } else if (code == 401) {
                    return new SnowType(context, SnowType.SNOW_LEVEL_2);
                } else if (code >= 402 && code <= 403) {
                    return new SnowType(context, SnowType.SNOW_LEVEL_3);
                } else if (code >= 404 && code <= 406) {
                    RainType rainSnowType = new RainType(context, RainType.RAIN_LEVEL_1, RainType.WIND_LEVEL_1);
                    rainSnowType.setSnowing(true);
                    return rainSnowType;
                } else {
                    return new SnowType(context, SnowType.SNOW_LEVEL_2);
                }
            } else if (code >= 500 && code <= 501) {//雾
                return new FogType(context);
            } else if (code == 502) {//霾
                return new HazeType(context);
            } else if (code >= 503 && code <= 508) {//各种沙尘暴
                return new SandstormType(context);
            } else if (code == 900) {//热
                return new SunnyType(context, info);
            } else if (code == 901) {//冷
                return new SnowType(context, SnowType.SNOW_LEVEL_1);
            } else {//未知
                return new SunnyType(context, info);
            }
        } else
            return null;
    }
}
