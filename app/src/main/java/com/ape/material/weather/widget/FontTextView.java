package com.ape.material.weather.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ape.material.weather.App;


public class FontTextView extends TextView {

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
//		setIncludeFontPadding(false);
        setTypeface(App.getTypeface());
    }

}
