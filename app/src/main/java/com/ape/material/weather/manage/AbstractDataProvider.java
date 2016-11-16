package com.ape.material.weather.manage;

import com.ape.material.weather.bean.City;

/**
 * Created by way on 2016/11/13.
 */

public abstract class AbstractDataProvider {
    public abstract int getCount();

    public abstract City getItem(int index);

    public abstract void removeItem(int position);

    public abstract void moveItem(int fromPosition, int toPosition);

    public abstract void swapItem(int fromPosition, int toPosition);

    public abstract int undoLastRemoval();


}
