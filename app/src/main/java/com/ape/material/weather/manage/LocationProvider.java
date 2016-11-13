package com.ape.material.weather.manage;

import com.ape.material.weather.bean.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by way on 2016/11/13.
 */

public class LocationProvider extends AbstractDataProvider {
    private ArrayList<City> mData;
    private ArrayList<City> mDeleteData;
    private City mLastRemovedData;
    private int mLastRemovedPosition = -1;

    public LocationProvider() {
        mData = new ArrayList<>();
        mDeleteData = new ArrayList<>();
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

    public int getCount() {
        return mData.size();
    }

    public City getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        return mData.get(index);
    }

    public int undoLastRemoval() {
        if (mLastRemovedData != null) {
            int insertedPosition;
            if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                insertedPosition = mLastRemovedPosition;
            } else {
                insertedPosition = mData.size();
            }

            mData.add(insertedPosition, mLastRemovedData);
            if (mDeleteData.contains(mLastRemovedData))
                mDeleteData.remove(mLastRemovedData);

            mLastRemovedData = null;
            mLastRemovedPosition = -1;

            return insertedPosition;
        } else {
            return -1;
        }
    }

    public boolean deleteLastRemoval() {
        if (mLastRemovedData != null && !mDeleteData.isEmpty()) {
            mLastRemovedData = null;
            mLastRemovedPosition = -1;
            mDeleteData.clear();
            return true;
        } else {
            return false;
        }
    }

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

    }

    public void removeItem(int position) {
        //noinspection UnnecessaryLocalVariable
        final City removedItem = mData.remove(position);
        mDeleteData.add(removedItem);
        mLastRemovedData = removedItem;
        mLastRemovedPosition = position;
    }

    public void clear() {
        mData.clear();
    }
}
