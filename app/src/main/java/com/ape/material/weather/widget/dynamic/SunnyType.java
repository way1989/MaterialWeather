package com.ape.material.weather.widget.dynamic;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.ape.material.weather.R;
import com.ape.material.weather.util.TimeUtils;


/**
 * Created by liyu on 2017/8/18.
 */

public class SunnyType extends BaseWeatherType {

    private static final float bitmapScale = 0.2f; //图片的缩小倍数
    private static final int colorDay = 0xFF51C0F8;
    private static final int colorNight = 0xFF7F9EE9;
    private static final int colorWaveStartNight = 0x1A3A66CF;
    private static final int colorWaveEndNightRear = 0x803A66CF;
    private static final int colorWaveEndNightFront = 0xE63A66CF;
    private static final int colorWaveStartDay = 0x1AFFFFFF;
    private static final int colorWaveEndDayRear = 0x80FFFFFF;
    private static final int colorWaveEndDayFront = 0xE6FFFFFF;
    private Paint mPaint;               // 画笔
    private Path mPathFront;            //近处的波浪 path
    private Path mPathRear;             //远处的波浪 path
    private Path sunPath;               // 太阳升起的半圆轨迹
    private PathMeasure sunMeasure;
    private float speed;                //振幅，用于初始时的动画效果
    private float[] pos;                // 当前点的实际位置
    private float[] tan;                // 当前点的切线写角度值,用于计算图片所需旋转的角度
    private Bitmap boat;             // 小船儿
    private Matrix mMatrix;             // 矩阵,用于对小船儿进行一些操作
    private PathMeasure measure;
    private float φ;
    private Shader shaderRear;
    private Shader shaderFront;
    private float boardSpeed;
    private float cloudShake;
    private float cloudSpeed;
    private float currentSunPosition;
    private float currentMoonPosition;

    private float[] sunPos;
    private float[] sunTan;

    private float[] moonPos;
    private float[] moonTan;

    private boolean isCloud = false;

    private Bitmap cloud;

    public SunnyType(Resources resources, ShortWeatherInfo info) {
        super(resources);
        mPathFront = new Path();
        mPathRear = new Path();
        sunPath = new Path();
        mPaint = new Paint();
        pos = new float[2];
        tan = new float[2];
        mMatrix = new Matrix();
        measure = new PathMeasure();
        sunMeasure = new PathMeasure();
        currentSunPosition = TimeUtils.getTimeDiffPercent(info.getSunrise(), info.getSunset());
        currentMoonPosition = TimeUtils.getTimeDiffPercent(info.getMoonrise(), info.getMoonset());
        if (currentSunPosition >= 0 && currentSunPosition <= 1) {
            setColor(colorDay);
            boat = BitmapFactory.decodeResource(resources, R.drawable.ic_boat_day);
        } else {
            setColor(colorNight);
            boat = BitmapFactory.decodeResource(resources, R.drawable.ic_boat_night);
        }

        cloud = BitmapFactory.decodeResource(resources, R.drawable.ic_cloud);

    }

    public boolean isCloud() {
        return isCloud;
    }

    public void setCloud(boolean cloud) {
        isCloud = cloud;
    }

    @Override
    public void onDrawElements(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        clearCanvas(canvas);
        canvas.drawColor(getDynamicColor());

        if (currentSunPosition >= 0 && currentSunPosition <= 1) {
            mPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));
            canvas.drawCircle(sunPos[0], sunPos[1], 40, mPaint);
            mPaint.setMaskFilter(null);
            if (shaderRear == null) {
                shaderRear = new LinearGradient(0, getHeight(), getWidth(), getHeight(), colorWaveStartDay, colorWaveEndDayRear, Shader.TileMode.CLAMP);
            }
            if (shaderFront == null) {
                shaderFront = new LinearGradient(0, getHeight(), getWidth(), getHeight(), colorWaveStartDay, colorWaveEndDayFront, Shader.TileMode.CLAMP);
            }
        } else if (currentMoonPosition >= 0 && currentMoonPosition <= 1) {
            mPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));
            canvas.drawCircle(moonPos[0], moonPos[1], 40, mPaint);
            mPaint.setColor(getDynamicColor());
            canvas.drawCircle(moonPos[0] + 20, moonPos[1] - 20, 40, mPaint);
            mPaint.setColor(Color.WHITE);
            mPaint.setMaskFilter(null);
            if (shaderRear == null) {
                shaderRear = new LinearGradient(0, getHeight(), getWidth(), getHeight(), colorWaveStartNight, colorWaveEndNightRear, Shader.TileMode.CLAMP);
            }
            if (shaderFront == null) {
                shaderFront = new LinearGradient(0, getHeight(), getWidth(), getHeight(), colorWaveStartNight, colorWaveEndNightFront, Shader.TileMode.CLAMP);
            }
        } else {
            mPaint.setMaskFilter(null);
            if (shaderRear == null) {
                shaderRear = new LinearGradient(0, getHeight(), getWidth(), getHeight(), colorWaveStartNight, colorWaveEndNightRear, Shader.TileMode.CLAMP);
            }
            if (shaderFront == null) {
                shaderFront = new LinearGradient(0, getHeight(), getWidth(), getHeight(), colorWaveStartNight, colorWaveEndNightFront, Shader.TileMode.CLAMP);
            }
        }

        φ -= 0.05f;

        float y, y2;

        double ω = 2 * Math.PI / getWidth();

        mPathRear.reset();
        mPathFront.reset();
        mPathFront.moveTo(-boat.getWidth() * bitmapScale, getHeight());
        mPathRear.moveTo(-boat.getWidth() * bitmapScale, getHeight());
        for (float x = -boat.getWidth() * bitmapScale; x <= getWidth() + boat.getWidth() * bitmapScale; x += 20) {
            /**
             *  y=Asin(ωx+φ)+k
             *  A—振幅越大，波形在y轴上最大与最小值的差值越大
             *  ω—角速度， 控制正弦周期(单位角度内震动的次数)
             *  φ—初相，反映在坐标系上则为图像的左右移动，通过不断改变φ,达到波浪移动效果
             *  k—偏距，反映在坐标系上则为图像的上移或下移。
             */
            y = (float) (speed * Math.cos(ω * x + φ) + getHeight() * 6 / 7);
            y2 = (float) (speed * Math.sin(ω * x + φ) + getHeight() * 6 / 7 - 8);
            mPathFront.lineTo(x, y);
            mPathRear.lineTo(x, y2);
        }

        mPathFront.lineTo(getWidth() + boat.getWidth() * bitmapScale, getHeight());
        mPathRear.lineTo(getWidth() + boat.getWidth() * bitmapScale, getHeight());

        mPaint.setShader(shaderRear);
        canvas.drawPath(mPathRear, mPaint);

        measure.setPath(mPathFront, false);
        measure.getPosTan(measure.getLength() * 0.618f * boardSpeed, pos, tan);
        mMatrix.reset();
        float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
        mMatrix.postScale(bitmapScale, bitmapScale);
        mMatrix.postRotate(degrees, boat.getWidth() * bitmapScale / 2, boat.getHeight() * bitmapScale / 2);
        mMatrix.postTranslate(pos[0] - boat.getWidth() / 2 * bitmapScale, pos[1] - boat.getHeight() * bitmapScale + 4);
        mPaint.setAlpha(255);
        canvas.drawBitmap(boat, mMatrix, mPaint);

        mPaint.setShader(shaderFront);
        canvas.drawPath(mPathFront, mPaint);

        if (!isCloud)
            return;

        if (currentSunPosition >= 0 && currentSunPosition <= 1) {
            mPaint.setAlpha(200);
        } else {
            mPaint.setAlpha(80);
        }
        mMatrix.reset();
        mMatrix.postScale(bitmapScale, bitmapScale);
        mMatrix.postTranslate(getWidth() / 2 * cloudSpeed, getHeight() / 2 + cloudShake);
        canvas.drawBitmap(cloud, mMatrix, mPaint);

    }

    @Override
    public void generateElements() {
        sunPath.reset();
        RectF rectF = new RectF(0 + 100, getHeight() * (8.5f / 10f) - getWidth() / 2, getWidth() - 100, getHeight() * (8.5f / 10f) + getWidth() / 2);
        sunPath.arcTo(rectF, 180, 180);
        sunMeasure.setPath(sunPath, false);
    }

    @Override
    public void startAnimation(final DynamicWeatherView dynamicWeatherView, int fromColor) {
        super.startAnimation(dynamicWeatherView, fromColor);
        sunPos = new float[2];
        sunTan = new float[2];
        moonPos = new float[2];
        moonTan = new float[2];
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(2000);
        animator.setRepeatCount(0);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                speed = (float) animation.getAnimatedValue() * 32;
            }
        });
        animator.start();

        ValueAnimator animator2 = ValueAnimator.ofFloat(1.5f, 1);
        animator2.setDuration(3000);
        animator2.setRepeatCount(0);
        animator2.setInterpolator(new DecelerateInterpolator());
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                boardSpeed = (float) animation.getAnimatedValue();
            }
        });
        animator2.start();

        ValueAnimator animator3 = ValueAnimator.ofFloat(-0.5f, 1);
        animator3.setDuration(3000);
        animator3.setRepeatCount(0);
        animator3.setInterpolator(new DecelerateInterpolator());
        animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cloudSpeed = (float) animation.getAnimatedValue();
            }
        });
        animator3.start();

        ValueAnimator sunAnimator = ValueAnimator.ofFloat(0, 1);
        sunAnimator.setDuration(3000);
        sunAnimator.setRepeatCount(0);
        sunAnimator.setInterpolator(PathInterpolatorCompat.create(0.5f, 1));
        sunAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sunMeasure.getPosTan(sunMeasure.getLength() * (float) animation.getAnimatedValue() * currentSunPosition, sunPos, sunTan);
                sunMeasure.getPosTan(sunMeasure.getLength() * (float) animation.getAnimatedValue() * currentMoonPosition, moonPos, moonTan);
            }
        });
        sunAnimator.start();

        ValueAnimator animatorCloud = ValueAnimator.ofFloat(-10, 10);
        animatorCloud.setDuration(2000);
        animatorCloud.setRepeatCount(-1);
        animatorCloud.setRepeatMode(ValueAnimator.REVERSE);
        animatorCloud.setInterpolator(new DecelerateInterpolator());
        animatorCloud.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cloudShake = (float) animation.getAnimatedValue();
            }
        });
        animatorCloud.start();

    }

    @Override
    public void endAnimation(DynamicWeatherView dynamicWeatherView, Animator.AnimatorListener listener) {
        super.endAnimation(dynamicWeatherView, null);

        ValueAnimator animator1 = ValueAnimator.ofFloat(currentSunPosition, 1);
        animator1.setInterpolator(PathInterpolatorCompat.create(0.5f, 1));
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sunMeasure.getPosTan(sunMeasure.getLength() * (float) animation.getAnimatedValue(), sunPos, sunTan);
            }
        });

        ValueAnimator animator2 = ValueAnimator.ofFloat(1, 0);
        animator2.setInterpolator(new AccelerateInterpolator());
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                boardSpeed = (float) animation.getAnimatedValue();
            }

        });

        ValueAnimator animator3 = ValueAnimator.ofFloat(currentMoonPosition, 1);
        animator3.setInterpolator(PathInterpolatorCompat.create(0.5f, 1));
        animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sunMeasure.getPosTan(sunMeasure.getLength() * (float) animation.getAnimatedValue(), moonPos, moonTan);
            }
        });

        ValueAnimator animator4 = ValueAnimator.ofFloat(1, 2f);
        animator4.setInterpolator(new AccelerateInterpolator());
        animator4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cloudSpeed = (float) animation.getAnimatedValue();
            }

        });

        AnimatorSet animSet = new AnimatorSet();
        animSet.play(animator1).with(animator2).with(animator3).with(animator4);
        animSet.setDuration(1000);
        if (listener != null) {
            animSet.addListener(listener);
        }
        animSet.start();
    }
}
