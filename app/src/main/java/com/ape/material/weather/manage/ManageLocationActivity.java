package com.ape.material.weather.manage;

import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ape.material.weather.R;
import com.ape.material.weather.base.BaseActivity;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.search.SearchCityActivity;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.List;

import butterknife.BindView;

public class ManageLocationActivity extends BaseActivity<ManageLocationPresenter, ManageLocationModel>
        implements ManageLocationContract.View {
    private static final String TAG = "ManageLocationActivity";
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private LocationAdapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
    private LocationProvider mProvider = new LocationProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.material_shadow_z3));

        mRecyclerViewDragDropManager.setOnItemDragEventListener(new RecyclerViewDragDropManager.OnItemDragEventListener() {
            @Override
            public void onItemDragStarted(int position) {
                Log.i(TAG, "onItemDragStarted... position = " + position);
            }

            @Override
            public void onItemDragPositionChanged(int fromPosition, int toPosition) {
                Log.i(TAG, "onItemDragPositionChanged... fromPosition = " + fromPosition + ", toPosition = " + toPosition);
            }

            @Override
            public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
                Log.i(TAG, "onItemDragFinished... fromPosition = " + fromPosition + ", toPosition = " + toPosition + ", result = " + result);
            }

            @Override
            public void onItemDragMoveDistanceUpdated(int offsetX, int offsetY) {
                Log.i(TAG, "onItemDragMoveDistanceUpdated... offsetX = " + offsetX + ", offsetY = " + offsetY);
            }
        });

        // swipe manager
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        //adapter
        final LocationAdapter myItemAdapter = new LocationAdapter(mProvider);
        myItemAdapter.setEventListener(new LocationAdapter.EventListener() {
            @Override
            public void onItemRemoved(int position) {
                ManageLocationActivity.this.onItemRemoved(position);
            }

            @Override
            public void onItemViewClicked(View v) {
                ManageLocationActivity.this.onItemViewClick(v);
            }
        });

        mAdapter = myItemAdapter;

        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter);      // wrap for dragging
        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mWrappedAdapter);      // wrap for swiping

        final GeneralItemAnimator animator = new DraggableItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        animator.setSupportsChangeAnimations(false);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView.addItemDecoration(new ItemShadowDecorator(
                    (NinePatchDrawable) ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.material_shadow_z1)));
        }
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat
                .getDrawable(getApplicationContext(), R.drawable.list_divider_h), true));

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop
        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);

        // for debugging
//        animator.setDebug(true);
//        animator.setMoveDuration(2000);
//        animator.setRemoveDuration(2000);
//        mRecyclerViewSwipeManager.setMoveToOutsideWindowAnimationDuration(2000);
//        mRecyclerViewSwipeManager.setReturnToDefaultPositionAnimationDuration(2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivity(new Intent(this, SearchCityActivity.class));
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getCities();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRecyclerViewDragDropManager.cancelDrag();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mRecyclerViewSwipeManager != null) {
            mRecyclerViewSwipeManager.release();
            mRecyclerViewSwipeManager = null;
        }

        if (mRecyclerViewTouchActionGuardManager != null) {
            mRecyclerViewTouchActionGuardManager.release();
            mRecyclerViewTouchActionGuardManager = null;
        }

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
        mLayoutManager = null;
    }

    private void onItemViewClick(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        if (position != RecyclerView.NO_POSITION) {
            onItemClicked(position);
        }
    }

    /**
     * This method will be called when a list item is removed
     *
     * @param position The position of the item within data set
     */
    public void onItemRemoved(int position) {
        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.container),
                R.string.snack_bar_text_item_removed,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.snack_bar_action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemUndoActionClicked();
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.snackbar_action_color_done));
        snackbar.show();
    }

    private void onItemUndoActionClicked() {
        int position = mProvider.undoLastRemoval();
        if (position >= 0) {
            mAdapter.notifyItemInserted(position);
        }
    }

    /**
     * This method will be called when a list item is clicked
     *
     * @param position The position of the item within data set
     */
    public void onItemClicked(int position) {
        AbstractDataProvider.Data data = mProvider.getItem(position);

        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            notifyItemChanged(position);
        }
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }


    public void notifyItemChanged(int position) {
        mAdapter.notifyItemChanged(position);
    }

    public void notifyItemInserted(int position) {
        mAdapter.notifyItemInserted(position);
        mRecyclerView.scrollToPosition(position);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        mPresenter.setVM(this, mModel);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manage_location;
    }

    @Override
    public void onCityChange(List<City> cities) {
        mProvider.setData(cities);
        mAdapter.setDatas(mProvider);
    }
}
