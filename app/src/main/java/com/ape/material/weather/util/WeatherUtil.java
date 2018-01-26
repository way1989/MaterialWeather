package com.ape.material.weather.util;


import com.ape.material.weather.bean.HeWeather;

/**
 * Created by liyu on 2016/11/11.
 */

public class WeatherUtil {

    private static WeatherUtil instance;


    private WeatherUtil() {

    }

    public static WeatherUtil getInstance() {
        if (instance == null) {
            synchronized (WeatherUtil.class) {
                instance = new WeatherUtil();
            }
        }
        return instance;
    }


    public String getShareMessage(HeWeather weather) {
        StringBuffer message = new StringBuffer();
        message.append(weather.getWeather().getBasic().getCity());
        message.append("天气：");
        message.append(weather.getWeather().getNow().getCond().getTxt());
        message.append("，");
        message.append(weather.getWeather().getNow().getFl()).append("℃");
        message.append("。");
        message.append("\r\n");
        message.append("发布：");
//        message.append("\r\n");
        message.append(weather.getWeather().getBasic().getUpdate().getLoc());
        message.append("\r\n");
        message.append("PM2.5：").append(weather.getWeather().getAqi().getCity().getPm25());
        message.append("，");
        message.append(weather.getWeather().getAqi().getCity().getQlty());
        message.append("。");
        message.append("\r\n");
        message.append("今天：");
        message.append(weather.getWeather().getDaily_forecast().get(0).getTmp().getMin()).append("℃-");
        message.append(weather.getWeather().getDaily_forecast().get(0).getTmp().getMax()).append("℃");
        message.append("，");
        message.append(weather.getWeather().getDaily_forecast().get(0).getCond().getTxt_d());
        message.append("\r\n");
        message.append("明天：");
        message.append(weather.getWeather().getDaily_forecast().get(1).getTmp().getMin()).append("℃-");
        message.append(weather.getWeather().getDaily_forecast().get(1).getTmp().getMax()).append("℃");
        message.append("，");
        message.append(weather.getWeather().getDaily_forecast().get(1).getCond().getTxt_d());

        return message.toString();
    }
}
