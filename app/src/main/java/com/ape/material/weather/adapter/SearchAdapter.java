package com.ape.material.weather.adapter;

import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.widget.TextView;

import com.ape.material.weather.R;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.SearchItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by android on 18-1-30.
 */

public class SearchAdapter extends BaseSectionQuickAdapter<SearchItem, BaseViewHolder> {


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param layoutResId      The layout resource id of each item.
     * @param sectionHeadResId The section head layout id for each item
     * @param data             A new list is created out of this one to avoid mutable list
     */
    public SearchAdapter(int layoutResId, int sectionHeadResId, List<SearchItem> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SearchItem item) {
        helper.setText(R.id.city_name_tv, item.header);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchItem item) {
        City city = item.t;
        TextView cityName = helper.getView(R.id.city_name_tv);
        TextView cityProv = helper.getView(R.id.city_prov_tv);
        cityName.setText(city.getCity());
        cityProv.setText(city.getProv());
    }
}
