package com.ape.material.weather.search;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

/**
 * Created by android on 16-11-15.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemHolder> {

    private Activity mContext;
    private List searchResults = Collections.emptyList();

    public SearchAdapter(Activity context) {
        this.mContext = context;

    }
    public void updateSearchResults(List searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public ItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}