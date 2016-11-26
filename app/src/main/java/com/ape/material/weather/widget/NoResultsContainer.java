package com.ape.material.weather.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ape.material.weather.R;


/**
 * This class is the default empty state view for most listviews/fragments It allows the ability to
 * set a main text, a main highlight text and a secondary text By default this container has some
 * strings loaded, but other classes can call the apis to change the text
 */
public class NoResultsContainer extends LinearLayout {
    public NoResultsContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMainText(final String text) {
        final TextView mainText = (TextView) findViewById(R.id.no_results_main_text);
        if (TextUtils.isEmpty(text)) {
            mainText.setText("");
            mainText.setVisibility(View.GONE);
        } else {
            mainText.setText(text);
            mainText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This changes the Main text (top-most text) of the empty container
     *
     * @param resId String resource id
     */
    public void setMainText(@StringRes final int resId) {
        final TextView mainText = (TextView) findViewById(R.id.no_results_main_text);
        if (resId < 0) {
            mainText.setText("");
            mainText.setVisibility(View.GONE);
        } else {
            mainText.setText(resId);
            mainText.setVisibility(View.VISIBLE);
        }
    }

    public void setMainHighlightText(final String text) {
        final TextView hightlightText = (TextView) findViewById(R.id.no_results_main_highlight_text);
        if (TextUtils.isEmpty(text)) {
            hightlightText.setText("");
            hightlightText.setVisibility(View.GONE);
        } else {
            hightlightText.setText(text);
            hightlightText.setVisibility(View.VISIBLE);
        }
    }

    public void setMainHighlightText(@StringRes final int resId) {
        final TextView hightlightText = (TextView) findViewById(R.id.no_results_main_highlight_text);
        if (resId < 0) {
            hightlightText.setText("");
            hightlightText.setVisibility(View.GONE);
        } else {
            hightlightText.setText(resId);
            hightlightText.setVisibility(View.VISIBLE);
        }
    }

    public void setSecondaryText(final String text) {
        final TextView secondaryText = (TextView) findViewById(R.id.no_results_secondary_text);
        if (TextUtils.isEmpty(text)) {
            secondaryText.setText("");
            secondaryText.setVisibility(View.GONE);
        } else {
            secondaryText.setText(text);
            secondaryText.setVisibility(View.VISIBLE);
        }
    }

    public void setSecondaryText(@StringRes final int resId) {
        final TextView secondaryText = (TextView) findViewById(R.id.no_results_secondary_text);
        if (resId < 0) {
            secondaryText.setText("");
            secondaryText.setVisibility(View.GONE);
        } else {
            secondaryText.setText(resId);
            secondaryText.setVisibility(View.VISIBLE);
        }
    }

    public void setLogo(@DrawableRes final int resId) {
        ImageView logoImage = ((ImageView) findViewById(R.id.no_results_logo));
        if (resId < 0) {
            logoImage.setVisibility(View.GONE);
        } else {
            logoImage.setImageResource(resId);
            logoImage.setVisibility(View.VISIBLE);
        }
    }

    public void setLogo(final Drawable logo) {
        ImageView logoImage = ((ImageView) findViewById(R.id.no_results_logo));
        if (logo == null) {
            logoImage.setVisibility(View.GONE);
        } else {
            logoImage.setImageDrawable(logo);
            logoImage.setVisibility(View.VISIBLE);
        }
    }

    public void setTextColor(@ColorInt int color) {
        ((TextView) findViewById(R.id.no_results_main_text)).setTextColor(color);
        ((TextView) findViewById(R.id.no_results_main_highlight_text)).setTextColor(color);
        ((TextView) findViewById(R.id.no_results_secondary_text)).setTextColor(color);
    }
}