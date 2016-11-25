package com.ape.material.weather.manage;

import com.ape.material.weather.bean.City;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by way on 2016/11/13.
 */

public class CityProvider extends AbstractDataProvider {
    private ArrayList<City> mData;
    private City mLastRemovedData;
    private int mLastRemovedPosition = -1;

    public CityProvider() {
        mData = new ArrayList<>();
    }

    public ArrayList<City> getData() {
        return mData;
    }

    public void setData(List<City> datas) {
        if (datas == null || datas.isEmpty())
            return;
        mData.clear();
        mData.addAll(datas);
    }
    public void addData(City city){
        mData.add(city);
    }

    public void clear() {
        mData.clear();
    }

    public City getLastRemovedData(){
        return mLastRemovedData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public City getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }
        return mData.get(index);
    }

    @Override
    public int undoLastRemoval() {
        if (mLastRemovedData != null) {
            int insertedPosition;
            if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                insertedPosition = mLastRemovedPosition;
            } else {
                insertedPosition = mData.size();
            }

            mData.add(insertedPosition, mLastRemovedData);

            mLastRemovedData = null;
            mLastRemovedPosition = -1;

            return insertedPosition;
        } else {
            return -1;
        }
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final City item = mData.remove(fromPosition);

        mData.add(toPosition, item);
        mLastRemovedPosition = -1;
    }

    @Override
    public void swapItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        Collections.swap(mData, toPosition, fromPosition);
        mLastRemovedPosition = -1;
    }

    @Override
    public void removeItem(int position) {
        //noinspection UnnecessaryLocalVariable
        final City removedItem = mData.remove(position);

        mLastRemovedData = removedItem;
        mLastRemovedPosition = position;
    }
}
