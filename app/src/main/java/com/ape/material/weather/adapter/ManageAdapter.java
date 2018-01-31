package com.ape.material.weather.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.ape.material.weather.R;
import com.ape.material.weather.bean.City;
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
        // set text
        tvName.setText(item.getIsLocation() == 1 ? getSpannable(tvName.getContext(), item.getCity())
                : item.getCity());
    }

    private SpannableString getSpannable(Context context, String name) {
        if (TextUtils.isEmpty(name)) {
            return new SpannableString(context.getString(R.string.auto_location));
        }
        SpannableString ss = new SpannableString(name + " ");
        Drawable drawable = context.getResources()
                .getDrawable(R.drawable.ic_location_on_black_18dp);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ss.setSpan(new ImageSpan(drawable), name.length(), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

}
