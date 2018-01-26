package com.ape.material.weather.manage;

import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ape.material.weather.AppComponent;
import com.ape.material.weather.R;
import com.ape.material.weather.base.BaseActivity;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.search.SearchCityActivity;
import com.ape.material.weather.util.AppConstant;
import com.ape.material.weather.util.RxBus;
import com.ape.material.weather.util.RxEvent;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.weavey.loading.lib.LoadingLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ManageActivity extends BaseActivity<ManagePresenter>
        implements ManageContract.View {
    private static final String TAG = "ManageActivity";
    private static final int REQUEST_CODE_CITY = 0;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.loading_layout)
    LoadingLayout mLoadingLayout;

    private RecyclerView.LayoutManager mLayoutManager;
    private CityAdapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;

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
                if (fromPosition != toPosition) {
                    mPresenter.swapCity(mAdapter.getData());
                }
            }

            @Override
            public void onItemDragMoveDistanceUpdated(int offsetX, int offsetY) {
                Log.i(TAG, "onItemDragMoveDistanceUpdated... offsetX = " + offsetX + ", offsetY = " + offsetY);
            }
        });

        // swipe manager
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        //adapter
        final CityAdapter myItemAdapter = new CityAdapter();
        myItemAdapter.setEventListener(new CityAdapter.EventListener() {
            @Override
            public void onItemRemoved(int position) {
                ManageActivity.this.onItemRemoved(position);
            }

            @Override
            public void onItemViewClicked(View v) {
                ManageActivity.this.onItemViewClick(v);
            }

            @Override
            public void onItemLocation(int position) {
                //Snackbar.make(mRecyclerView, "detecting...", Snackbar.LENGTH_SHORT).show();
                mPresenter.getLocation();
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

        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat
                .getDrawable(getApplicationContext(), R.drawable.list_divider_h), true));

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop
        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CITY && resultCode == RESULT_OK) {
            City city = (City) data.getSerializableExtra(AppConstant.ARG_CITY_KEY);
            if (city != null) {
                mAdapter.addData(city);
                RxEvent.MainEvent event = new RxEvent.MainEvent(mAdapter.getData(), Integer.MIN_VALUE);
                RxBus.getInstance().post(event);
            }
        }
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
        int position = (int) v.getTag();
//                mRecyclerView.getChildAdapterPosition(v);
        if (position != RecyclerView.NO_POSITION) {
            //onItemClicked(position);
            RxBus.getInstance().post(new RxEvent.MainEvent(null, position));
            finish();
        }
    }

    /**
     * This method will be called when a list item is removed
     *
     * @param position The position of the item within data set
     */
    public void onItemRemoved(int position) {
        final City city = mAdapter.getLastRemovedData();//ready to delete
        if (city == null) return;
        mPresenter.deleteCity(city);
        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.container),
                R.string.snack_bar_text_item_removed,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.snack_bar_action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemUndoActionClicked(city);
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.snackbar_action_color_done));
        snackbar.show();
    }

    private void onItemUndoActionClicked(City city) {
        mPresenter.undoCity(city);
        int position = mAdapter.undoLastRemoval();
        if (position >= 0) {
            mAdapter.notifyItemInserted(position);
        }
    }

    public void notifyItemChanged(int position) {
        mAdapter.notifyItemChanged(position);
    }

    public void notifyItemInserted(int position) {
        mAdapter.notifyItemInserted(position);
        mRecyclerView.scrollToPosition(position);
    }

    @Override
    protected void initPresenter(AppComponent appComponent) {
        super.initPresenter(appComponent);
        DaggerManageComponent.builder().appComponent(appComponent)
                .managePresenterModule(new ManagePresenterModule(this)).build().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manage_location;
    }

    @Override
    public void onCityChange(List<City> cities) {
        if (cities != null && cities.size() > 0) {
            mLoadingLayout.setStatus(LoadingLayout.Success);
        } else {
            mLoadingLayout.setStatus(LoadingLayout.Empty);
        }
        mAdapter.setDatas(cities);
    }

    @Override
    public void onCityModify() {
        RxEvent.MainEvent event = new RxEvent.MainEvent(mAdapter.getData(), Integer.MIN_VALUE);
        RxBus.getInstance().post(event);
    }

    @Override
    public void onLocationChanged(City city) {
        mPresenter.getCities();
        if (city != null)
            Snackbar.make(mRecyclerView, getString(R.string.relocation_toast, city.getCity()),
                    Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        mLoadingLayout.setStatus(LoadingLayout.Loading);
    }

    @OnClick(R.id.fab)
    public void onClick() {
        startActivityForResult(new Intent(this, SearchCityActivity.class), REQUEST_CODE_CITY);
    }
}
