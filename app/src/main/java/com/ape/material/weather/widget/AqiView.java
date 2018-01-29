package com.ape.material.weather.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.ape.material.weather.bean.HeWeather;


/**
 * 空气质量的弧形“表” 10行 120dp
 *
 * @author Mixiaoxiao
 */
public class AqiView extends View {
    // private float lineSize;//每一行高度
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private RectF rectF = new RectF();
    private HeWeather.HeWeather5Bean.AqiBean.CityBean aqiCity;

    public AqiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint.setTextAlign(Align.CENTER);
        if (isInEditMode()) {
            return;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);

        final float w = getWidth();
        final float h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        final float lineSize = h / 10f;// 大约是12dp
        if (aqiCity == null) {
            textPaint.setStyle(Style.FILL);
            textPaint.setTextSize(lineSize * 1.25f);
            textPaint.setColor(0xaaffffff);
            canvas.drawText("暂无数据", w / 2f, h / 2f, textPaint);
            return;
        }
        float currAqiPercent = -1f;
        try {
            currAqiPercent = Float.valueOf(aqiCity.getAqi()) / 500f;// 污染%
            currAqiPercent = Math.min(currAqiPercent, 1f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // canvas.drawColor(0x33ffffff);

        float aqiArcRadius = lineSize * 4f;
        textPaint.setStyle(Style.STROKE);
        textPaint.setStrokeWidth(lineSize * 1);
        textPaint.setColor(0x55ffffff);
        rectF.set(-aqiArcRadius, -aqiArcRadius, aqiArcRadius, aqiArcRadius);
        final int saveCount = canvas.save();
        canvas.translate(w / 2f, h / 2f);
        // draw aqi restPercent arc
        final float startAngle = -210f;
        final float sweepAngle = 240f;
        canvas.drawArc(rectF, startAngle + sweepAngle * currAqiPercent, sweepAngle * (1f - currAqiPercent), false,
                textPaint);
        if (currAqiPercent >= 0f) {
            // draw aqi aqiPercent arc
            textPaint.setColor(0x99ffffff);
            canvas.drawArc(rectF, startAngle, sweepAngle * currAqiPercent, false, textPaint);
            // draw aqi arc center circle
            textPaint.setColor(0xffffffff);
            textPaint.setStrokeWidth(lineSize / 8f);
            canvas.drawCircle(0, 0, lineSize / 3f, textPaint);
            // draw aqi number and text
            textPaint.setStyle(Style.FILL);
            textPaint.setTextSize(lineSize * 1.5f);
            textPaint.setColor(0xffffffff);
            try {
                canvas.drawText(aqiCity.getAqi() + "", 0, lineSize * 3, textPaint);
            } catch (Exception e) {
                e.printStackTrace();
            }
            textPaint.setTextSize(lineSize * 1f);
            textPaint.setColor(0x88ffffff);
            try {
                canvas.drawText(aqiCity.getQlty() + "", 0, lineSize * 4.25f, textPaint);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // draw the aqi line
            canvas.rotate(startAngle + sweepAngle * currAqiPercent - 180f);
            textPaint.setStyle(Style.STROKE);
            textPaint.setColor(0xffffffff);
            float startX = lineSize / 3f;
            canvas.drawLine(-startX, 0, -lineSize * 4.5f, 0, textPaint);
        }
        canvas.restoreToCount(saveCount);
    }

    public void setData(HeWeather.HeWeather5Bean.AqiBean aqi) {
        if (aqi != null && aqi.getCity() != null) {
            this.aqiCity = aqi.getCity();
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // this.lineSize = h / 10f;
    }

}
