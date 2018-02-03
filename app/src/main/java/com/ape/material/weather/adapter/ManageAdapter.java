package com.ape.material.weather.adapter;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ape.material.weather.R;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.util.FormatUtil;
import com.ape.material.weather.util.UiUtil;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by android on 18-1-30.
 */

public class ManageAdapter extends BaseItemDraggableAdapter<City, BaseViewHolder> {
    public ManageAdapter(@LayoutRes int layoutResId, List<City> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, City item) {
        TextView tvName = helper.getView(android.R.id.text1);
        TextView tvWeather = helper.getView(android.R.id.text2);
        ImageView tvIcon = helper.getView(android.R.id.icon);
        CharSequence name = item.getCity();
        if (item.getIsLocation() == 1) {
            name = UiUtil.getNameWithIcon(item.getCity(), tvName.getContext().getDrawable(R.drawable.ic_location_on_black_18dp));
            helper.getView(R.id.drag_handle).setAlpha(0.2f);
        } else {
            helper.getView(R.id.drag_handle).setAlpha(1.0f);
        }
        // set text
        tvName.setText(name);
        tvWeather.setText(item.getCodeTxt() + " " + item.getTmp()+ "℃");
        tvIcon.setImageResource(FormatUtil.convertWeatherIcon(item.getCode()));
    }


}
