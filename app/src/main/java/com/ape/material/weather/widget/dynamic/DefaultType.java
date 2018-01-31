package com.ape.material.weather.widget.dynamic;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;

/**
 * 默认动态天气
 * Created by liyu on 2017/11/13.
 */

public class DefaultType extends BaseWeatherType {

    public DefaultType(Resources resources) {
        super(resources);
        setColor(0xFF51C0F8);
    }

    @Override
    public void onDrawElements(Canvas canvas) {
        clearCanvas(canvas);
        canvas.drawColor(getDynamicColor());
    }

    @Override
    public void generateElements() {

    }

    @Override
    public void endAnimation(DynamicWeatherView dynamicWeatherView, Animator.AnimatorListener listener) {
        super.endAnimation(dynamicWeatherView, listener);
        if (listener != null) {
            listener.onAnimationEnd(null);
        }
    }
}
