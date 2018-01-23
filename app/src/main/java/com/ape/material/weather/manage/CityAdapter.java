package com.ape.material.weather.manage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ape.material.weather.R;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.util.ViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by way on 2016/11/13.
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.MyViewHolder>
        implements DraggableItemAdapter<CityAdapter.MyViewHolder>,
        SwipeableItemAdapter<CityAdapter.MyViewHolder> {
    private static final String TAG = "CityAdapter";
    private CityProvider mProvider;
    private EventListener mEventListener;
    private View.OnClickListener mItemViewOnClickListener;

    public CityAdapter() {
        mProvider = new CityProvider();
        mItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(v);
            }
        };

        // DraggableItemAdapter and SwipeableItemAdapter require stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    public void setDatas(List<City> datas) {
        mProvider.setData(datas);
        notifyDataSetChanged();
    }

    private void onItemViewClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(v); // true --- pinned
        }
    }

    @Override
    public long getItemId(int position) {
        City city = mProvider.getItem(position);
        return city.getAreaId() == null ? position : city.getAreaId().hashCode();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item_draggable, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final City item = mProvider.getItem(position);

        // set listeners
        // (if the item is *pinned*, click event comes to the itemView)
        holder.mContainer.setTag(position);
        holder.mContainer.setOnClickListener(mItemViewOnClickListener);

        // set text
        holder.mTextView.setText(item.isLocation() ? getSpannable(holder.mTextView.getContext(),
                item.getCity()) : item.getCity());

        // set background resource (target view ID: container)
        /*final int dragState = holder.getDragStateFlags();
        final int swipeState = holder.getSwipeStateFlags();

        if (((dragState & DraggableItemConstants.STATE_FLAG_IS_UPDATED) != 0) ||
                ((swipeState & SwipeableItemConstants.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & DraggableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & DraggableItemConstants.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else if ((swipeState & SwipeableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_swiping_active_state;
            } else if ((swipeState & SwipeableItemConstants.STATE_FLAG_SWIPING) != 0) {
                bgResId = R.drawable.bg_item_swiping_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }*/

        // set swiping properties
        holder.setSwipeItemHorizontalSlideAmount(0);
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

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }

        mProvider.moveItem(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {

    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {

    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        if (onCheckCanStartDrag(holder, position, x, y)) {
            return SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_H;
        } else {
            //return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
            return SwipeableItemConstants.REACTION_CAN_SWIPE_LEFT;
        }
    }

    @Override
    public void onSwipeItemStarted(MyViewHolder holder, int position) {

    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {
        int bgRes = 0;
        switch (type) {
            case SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_neutral;
                break;
            case SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                City city = getItem(position);
                bgRes = city.isLocation() ? R.drawable.bg_swipe_item_left_location
                        : R.drawable.bg_swipe_item_left;
                break;
            case SwipeableItemConstants.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_right;
                break;
        }

        holder.itemView.setBackgroundResource(bgRes);
    }

    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, final int position, int result) {
        Log.d(TAG, "onSwipeItem(position = " + position + ", result = " + result + ")");

        switch (result) {
            // swipe left or right to delete
            case SwipeableItemConstants.RESULT_SWIPED_RIGHT:
            case SwipeableItemConstants.RESULT_SWIPED_LEFT:
                return new SwipeLeftResultAction(this, position);
            // other --- do nothing
            case SwipeableItemConstants.RESULT_CANCELED:
            default:
                return null;

        }
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    public ArrayList<City> getData() {
        return mProvider.getData();
    }

    public void addData(City city) {
        mProvider.addData(city);
        notifyDataSetChanged();
    }

    public City getLastRemovedData() {
        return mProvider.getLastRemovedData();
    }

    public int undoLastRemoval() {
        return mProvider.undoLastRemoval();
    }

    public City getItem(int position) {
        return mProvider.getItem(position);
    }

    public interface EventListener {
        void onItemRemoved(int position);

        void onItemViewClicked(View v);

        void onItemLocation(int position);
    }

    public static class MyViewHolder extends AbstractDraggableSwipeableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public TextView mTextView;

        public MyViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mTextView = (TextView) v.findViewById(android.R.id.text1);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }

    private static class SwipeLeftResultAction extends SwipeResultActionRemoveItem {
        private final int mPosition;
        private CityAdapter mAdapter;
        private boolean isLocation;

        SwipeLeftResultAction(CityAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            City item = mAdapter.getItem(mPosition);

            if (item.isLocation()) {
                item.setCity("");
                mAdapter.notifyItemChanged(mPosition);
                isLocation = true;
            } else {
                mAdapter.mProvider.removeItem(mPosition);
                mAdapter.notifyItemRemoved(mPosition);
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();
            if (mAdapter.mEventListener != null) {
                if (isLocation) {
                    mAdapter.mEventListener.onItemLocation(mPosition);
                } else {
                    mAdapter.mEventListener.onItemRemoved(mPosition);
                }
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

}
