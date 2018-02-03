package com.ape.material.weather.adapter;


import com.ape.material.weather.R;
import com.ape.material.weather.bean.Suggestion;
import com.ape.material.weather.widget.CircleImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by liyu on 2017/4/1.
 */

public class SuggestionAdapter extends BaseQuickAdapter<Suggestion, BaseViewHolder> {

    public SuggestionAdapter(int layoutResId, List<Suggestion> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, Suggestion item) {
        CircleImageView circleImageView = holder.getView(R.id.civ_suggesstion);
        holder.setText(R.id.tvName, item.getTitle());
        holder.setText(R.id.tvMsg, item.getMsg());
        circleImageView.setFillColor(item.getIconBackgroudColor());
        circleImageView.setImageResource(item.getIcon());
    }
}
