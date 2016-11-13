package com.ape.material.weather.manage;

/**
 * Created by way on 2016/11/13.
 */

public abstract class AbstractDataProvider {
    public abstract int getCount();

    public abstract Data getItem(int index);

    public abstract void removeItem(int position);

    public abstract void moveItem(int fromPosition, int toPosition);

    public abstract void swapItem(int fromPosition, int toPosition);

    public abstract int undoLastRemoval();

    public static abstract class Data {
        public abstract long getId();

        public abstract boolean isSectionHeader();

        public abstract int getViewType();

        public abstract String getText();

        public abstract boolean isPinned();

        public abstract void setPinned(boolean pinned);
    }
}
