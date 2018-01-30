package com.ape.material.weather.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.ape.material.weather.R;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.adapter.ManageAdapter;
import com.ape.material.weather.util.AppConstant;
import com.ape.material.weather.util.RxBus;
import com.ape.material.weather.util.RxEvent;
import com.ape.material.weather.util.RxSchedulers;
import com.ape.material.weather.widget.SimpleListDividerDecorator;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.jakewharton.rxbinding2.view.RxView;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.weavey.loading.lib.LoadingLayout;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

public class ManageActivity extends BaseActivity {
    private static final String TAG = "ManageActivity";
    private static final int REQUEST_CODE_CITY = 0;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.loading_layout)
    LoadingLayout mLoadingLayout;

    private ManageAdapter mAdapter;

    private boolean dataChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        RxView.clicks(mFab)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        startActivityForResult(new Intent(ManageActivity.this,
                                SearchCityActivity.class), REQUEST_CODE_CITY);
                    }
                });
        mAdapter = new ManageAdapter(R.layout.list_item_draggable, null);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat
                .getDrawable(getApplicationContext(), R.drawable.list_divider_h), true));
        mRecyclerView.setAdapter(mAdapter);

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapter) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
                if (source.getAdapterPosition() == 0 || target.getAdapterPosition() == 0)
                    return false;
                else
                    return super.onMove(recyclerView, source, target);
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.getAdapterPosition() == 0)
                    return makeMovementFlags(0, 0);
                else
                    return super.getMovementFlags(recyclerView, viewHolder);
            }


        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START | ItemTouchHelper.END);

        // 开启拖拽
        mAdapter.enableDragItem(itemTouchHelper, R.id.drag_handle, false);
        mAdapter.setOnItemDragListener(new OnItemDragListener() {
            private int dragStartPosition = -1;

            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
                dragStartPosition = pos;
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {

            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
                if (dragStartPosition != pos) {
                    dataChanged = true;
                    mViewModel.swapCity(mAdapter.getData())
                            .compose(ManageActivity.this.<Boolean>bindUntilEvent(ActivityEvent.DESTROY))
                            .compose(RxSchedulers.<Boolean>io_main())
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean aBoolean) throws Exception {
                                    RxEvent.MainEvent event = new RxEvent.MainEvent(mAdapter.getData(), Integer.MIN_VALUE);
                                    RxBus.getInstance().post(event);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {

                                }
                            });

                }
            }
        });

        // 开启滑动删除
        mAdapter.enableSwipeItem();
        mAdapter.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {

            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {

            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                City city = mAdapter.getItem(pos);
                onItemRemoved(city);
                //DataSupport.deleteAll(WeatherCity.class, "cityName = ?", adapter.getItem(pos).getCityName());

                dataChanged = true;
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

            }
        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.d(TAG, "onItemClick: city = " + mAdapter.getItem(position).getCity());
                //selectedItem = position;
                RxBus.getInstance().post(new RxEvent.MainEvent(null, position));
                finish();
            }
        });

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
        getCities();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onItemRemoved(final City city) {
        if (city == null) return;
        mViewModel.deleteCity(city)
                .compose(this.<Boolean>bindUntilEvent(ActivityEvent.DESTROY))
                .compose(RxSchedulers.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Snackbar.make(mRecyclerView, city.getCity() + " 删除成功!",
                                Snackbar.LENGTH_LONG).show();
                        onCityModify();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manage_location;
    }

    public void onCityChange(List<City> cities) {
        if (cities != null && cities.size() > 0) {
            mLoadingLayout.setStatus(LoadingLayout.Success);
        } else {
            mLoadingLayout.setStatus(LoadingLayout.Empty);
        }
        mAdapter.setNewData(cities);
    }

    public void onCityModify() {
        RxEvent.MainEvent event = new RxEvent.MainEvent(mAdapter.getData(), Integer.MIN_VALUE);
        RxBus.getInstance().post(event);
    }

    public void showLoading() {
        mLoadingLayout.setStatus(LoadingLayout.Loading);
    }

    void getCities() {
        showLoading();
        mViewModel.getCities()
                .compose(this.<List<City>>bindUntilEvent(ActivityEvent.DESTROY))
                .compose(RxSchedulers.<List<City>>io_main())
                .subscribe(new Consumer<List<City>>() {
                    @Override
                    public void accept(List<City> cities) throws Exception {
                        onCityChange(cities);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "accept: getCities ", throwable);
                    }
                });
    }

}
