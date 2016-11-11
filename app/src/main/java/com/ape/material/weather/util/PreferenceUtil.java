package com.ape.material.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by android on 16-11-11.
 */

public class PreferenceUtil {
    private static final String PERFERENCE_FIRST_RUN = "perference_first_run";

    private static PreferenceUtil sInstance;

    private volatile static SharedPreferences mPreferences;

    private PreferenceUtil(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceUtil getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (PreferenceUtil.class) {
                if (sInstance == null)
                    sInstance = new PreferenceUtil(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    public long getCacheTime(String key) {
        return mPreferences.getLong(key, 0L);
    }

    public void setCacheTime(String key, long time) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(key, time);
        editor.apply();
    }

}
