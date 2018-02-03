package com.ape.material.weather.util;


import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.bean.Suggestion;

import java.util.ArrayList;
import java.util.List;

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

    public static List<Suggestion> getSuggestion(HeWeather weather) {
        HeWeather.HeWeather5Bean.SuggestionBean suggestion = weather.getWeather().getSuggestion();
        List<Suggestion> suggestionList = new ArrayList<>();
        Suggestion air = new Suggestion();
        air.setTitle(Suggestion.SUGGESTION_AIR);
        air.setMsg(suggestion == null || suggestion.getAir() == null ? "未知" : suggestion.getAir().getBrf());
        suggestionList.add(air);

        Suggestion comf = new Suggestion();
        comf.setTitle(Suggestion.SUGGESTION_COMF);
        comf.setMsg(suggestion == null || suggestion.getComf() == null ? "未知" : suggestion.getComf().getBrf());
        suggestionList.add(comf);

        Suggestion carWash = new Suggestion();
        carWash.setTitle(Suggestion.SUGGESTION_CW);
        carWash.setMsg(suggestion == null || suggestion.getCw() == null ? "未知" : suggestion.getCw().getBrf());
        suggestionList.add(carWash);

        Suggestion drsg = new Suggestion();
        drsg.setTitle(Suggestion.SUGGESTION_DRSG);
        drsg.setMsg(suggestion == null || suggestion.getDrsg() == null ? "未知" : suggestion.getDrsg().getBrf());
        suggestionList.add(drsg);

        Suggestion flu = new Suggestion();
        flu.setTitle(Suggestion.SUGGESTION_FLU);
        flu.setMsg(suggestion == null || suggestion.getFlu() == null ? "未知" : suggestion.getFlu().getBrf());
        suggestionList.add(flu);

        Suggestion sport = new Suggestion();
        sport.setTitle(Suggestion.SUGGESTION_SPORT);
        sport.setMsg(suggestion == null || suggestion.getSport() == null ? "未知" : suggestion.getSport().getBrf());
        suggestionList.add(sport);

        Suggestion trav = new Suggestion();
        trav.setTitle(Suggestion.SUGGESTION_TRAV);
        trav.setMsg(suggestion == null || suggestion.getTrav() == null ? "未知" : suggestion.getTrav().getBrf());
        suggestionList.add(trav);

        Suggestion uv = new Suggestion();
        uv.setTitle(Suggestion.SUGGESTION_UV);
        uv.setMsg(suggestion == null || suggestion.getUv() == null ? "未知" : suggestion.getUv().getBrf());
        suggestionList.add(uv);

        return suggestionList;
    }

}
