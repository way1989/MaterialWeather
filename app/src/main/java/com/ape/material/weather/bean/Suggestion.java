package com.ape.material.weather.bean;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringDef;

import com.ape.material.weather.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by way on 2018/2/4.
 */

public class Suggestion {
    public static final String SUGGESTION_AIR = "空气";
    public static final String SUGGESTION_COMF = "舒适度";
    public static final String SUGGESTION_CW = "洗车";
    public static final String SUGGESTION_DRSG = "穿衣";
    public static final String SUGGESTION_FLU = "感冒";
    public static final String SUGGESTION_SPORT = "运动";
    public static final String SUGGESTION_TRAV = "旅游";
    public static final String SUGGESTION_UV = "紫外线";
    private String title;
    private String msg;
    private
    @DrawableRes
    int icon;
    private int iconBackgroudColor;

    public String getTitle() {
        return title;
    }

    public void setTitle(@SuggestionType String title) {
        this.title = title;
        switch (title) {
            case SUGGESTION_AIR:
                setIcon(R.drawable.ic_air);
                setIconBackgroudColor(0xFF7F9EE9);
                break;
            case SUGGESTION_COMF:
                setIcon(R.drawable.ic_comf);
                setIconBackgroudColor(0xFFE99E3C);
                break;
            case SUGGESTION_CW:
                setIcon(R.drawable.ic_cw);
                setIconBackgroudColor(0xFF62B1FF);
                break;
            case SUGGESTION_DRSG:
                setIcon(R.drawable.ic_drsg);
                setIconBackgroudColor(0xFF8FC55F);
                break;
            case SUGGESTION_FLU:
                setIcon(R.drawable.ic_flu);
                setIconBackgroudColor(0xFFF98178);
                break;
            case SUGGESTION_SPORT:
                setIcon(R.drawable.ic_sport);
                setIconBackgroudColor(0xFFB3CA60);
                break;
            case SUGGESTION_TRAV:
                setIcon(R.drawable.ic_trav);
                setIconBackgroudColor(0xFFFD6C35);
                break;
            case SUGGESTION_UV:
                setIcon(R.drawable.ic_uv);
                setIconBackgroudColor(0xFFF0AB2A);
                break;
            default:
                setIcon(R.drawable.ic_air);
                setIconBackgroudColor(0xFF7F9EE9);


        }
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIconBackgroudColor() {
        return iconBackgroudColor;
    }

    public void setIconBackgroudColor(int iconBackgroudColor) {
        this.iconBackgroudColor = iconBackgroudColor;
    }

    @StringDef({SUGGESTION_AIR, SUGGESTION_COMF, SUGGESTION_CW, SUGGESTION_DRSG, SUGGESTION_FLU, SUGGESTION_SPORT, SUGGESTION_TRAV, SUGGESTION_UV})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SuggestionType {
    }
}
