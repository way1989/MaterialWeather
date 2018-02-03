package com.ape.material.weather.adapter;

import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.widget.TextView;

import com.ape.material.weather.R;
import com.ape.material.weather.bean.City;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by android on 18-1-30.
 */

public class SearchAdapter extends BaseQuickAdapter<City, BaseViewHolder> {
    public SearchAdapter(@LayoutRes int layoutResId, List<City> cities) {
        super(layoutResId, cities);
    }

    @Override
    protected void convert(BaseViewHolder helper, City item) {
        TextView cityName = helper.getView(R.id.city_name_tv);
        TextView cityProv = helper.getView(R.id.city_prov_tv);
        cityName.setText(item.getCity());
        cityProv.setText(item.getProv());
    }
}
