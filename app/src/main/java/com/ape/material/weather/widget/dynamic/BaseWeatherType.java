package com.ape.material.weather.widget.dynamic;

import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.animation.LinearInterpolator;

import java.util.Random;

/**
 * Created by liyu on 2017/8/16.
 */

public abstract class BaseWeatherType implements WeatherHandler {
    protected static final long START_ANIM_DURATION = 3000L;
    protected static final long END_ANIM_DURATION = 400L;
    protected int color;
    private int dynamicColor;
    protected Resources mResources;
    private int mWidth;
    private int mHeight;

    public BaseWeatherType(Resources resources) {
        mResources = resources;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getDynamicColor() {
        return dynamicColor;
    }

    public abstract void generateElements();

    public void startAnimation(int fromColor) {
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, color);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1000);
        animator.setRepeatCount(0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dynamicColor = (int) animation.getAnimatedValue();
            }
        });
        animator.start();
    }

    public abstract void endAnimation(AnimatorListenerAdapter listener);

    @Override
    public void onSizeChanged(int w, int h) {
        mWidth = w;
        mHeight = h;
        generateElements();
    }

    protected void clearCanvas(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    protected int getRandom(int min, int max) {
        if (max < min) {
            return 1;
        }
        return min + new Random().nextInt(max - min);
    }

    public int dp2px(float dpValue) {
        final float scale = mResources.getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
