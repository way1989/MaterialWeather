package com.ape.material.weather.search;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ape.material.weather.R;
import com.ape.material.weather.bean.City;

import java.util.Collections;
import java.util.List;

/**
 * Created by android on 16-11-15.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemHolder> {

    private OnItemClickListener mListener;
    private List<City> searchResults = Collections.emptyList();

    public SearchAdapter(OnItemClickListener listener) {
        mListener = listener;
    }

    public void updateSearchResults(List searchResults) {
        this.searchResults = searchResults;
        notifyDataSetChanged();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        City city = searchResults.get(position);
        holder.itemView.setTag(city);
        holder.cityName.setText(city.getCity());
        if (!TextUtils.isEmpty(city.getProv())) {
            holder.cityProv.setVisibility(View.VISIBLE);
            holder.cityProv.setText(city.getProv());
        } else {
            holder.cityProv.setVisibility(View.GONE);
            holder.cityProv.setText("");
        }
        holder.cityCountry.setText(city.getCountry());
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public void clear() {
        searchResults.clear();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(City city);
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView cityName, cityProv, cityCountry;

        public ItemHolder(View itemView) {
            super(itemView);
            cityName = (TextView) itemView.findViewById(R.id.city_name_tv);
            cityProv = (TextView) itemView.findViewById(R.id.city_prov_tv);
            cityCountry = (TextView) itemView.findViewById(R.id.city_country_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            City city = (City) v.getTag();
            if (mListener != null)
                mListener.onItemClick(city);
        }
    }

}