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
import com.ape.material.weather.adapter.ManageAdapter;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.bean.HeWeather;
import com.ape.material.weather.util.AppConstant;
import com.ape.material.weather.util.RxSchedulers;
import com.ape.material.weather.widget.SimpleListDividerDecorator;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.jakewharton.rxbinding2.view.RxView;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.weavey.loading.lib.LoadingLayout;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ManageActivity extends BaseActivity {
    public static final String EXTRA_DATA_CHANGED = "extra_data_changed";
    public static final String EXTRA_SELECTED_ITEM = "extra_selected_item";
    private static final String TAG = "ManageActivity";
    private static final int REQUEST_CODE_CITY = 0;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.loading_layout)
    LoadingLayout mLoadingLayout;

    private ManageAdapter mAdapter;

    private boolean mDataChanged;
    private int mSelectedItem;

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
        mAdapter = new ManageAdapter(R.layout.item_manage_city, null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat
                .getDrawable(getApplicationContext(), R.drawable.list_divider_h), false));
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
                    mDataChanged = true;
                    onItemSwap();
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
                mDataChanged = true;
                onItemRemoved(city);
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

            }
        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.d(TAG, "onItemClick: city = " + mAdapter.getItem(position));
                mSelectedItem = position;
                onBackPressed();
            }
        });
        getCities();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra(EXTRA_SELECTED_ITEM, mSelectedItem);
        i.putExtra(EXTRA_DATA_CHANGED, mDataChanged);
        setResult(RESULT_OK, i);
        mSelectedItem = -1;
        mDataChanged = false;
        super.onBackPressed();
    }

    private void onItemSwap() {
        final List<City> data = mAdapter.getData();
        mViewModel.swapCity(data)
                .compose(ManageActivity.this.<Boolean>bindUntilEvent(ActivityEvent.DESTROY))
                .compose(RxSchedulers.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        Log.d(TAG, "accept: onItemSwap result = " + result);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "accept: onItemSwap...", throwable);
                    }
                });
    }

    public void onItemRemoved(final City city) {
        mViewModel.deleteCity(city)
                .compose(this.<Boolean>bindUntilEvent(ActivityEvent.DESTROY))
                .compose(RxSchedulers.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        Snackbar.make(mRecyclerView, city.getCity() + " 删除成功!",
                                Snackbar.LENGTH_LONG).show();
                        Log.d(TAG, "accept: onItemRemoved city = " + city.getCity()
                                + " result = " + result);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "accept: onItemRemoved...", throwable);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CITY && resultCode == RESULT_OK) {
            City city = (City) data.getSerializableExtra(AppConstant.ARG_CITY_KEY);
            if (city != null) {
                mDataChanged = true;
                mAdapter.addData(city);
                getWeather(city.getAreaId(), true);
            }
        }
    }
    private void getWeather(String areaId, boolean force) {
        Log.i(TAG, "getWeather... areaId = " + areaId);
        mViewModel.getWeather(areaId, force)
                .compose(this.<HeWeather>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HeWeather>() {
                    @Override
                    public void accept(HeWeather heWeather) throws Exception {
                        Log.d(TAG, "getWeather: onNext weather isOK = " + heWeather.isOK());
                        if (heWeather.isOK()) {
                            String code = heWeather.getWeather().getNow().getCond().getCode();
                            String codeTxt = heWeather.getWeather().getNow().getCond().getTxt();
                            String tmp = heWeather.getWeather().getNow().getTmp();
                            Log.d(TAG, "getWeather: codeTxt = " + codeTxt + ", code = " + code + ", tmp = " + tmp);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "getWeather onError: ", throwable);
                    }
                });

    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_manage_city;
    }

    public void onCityChange(List<City> cities) {
        if (cities != null && cities.size() > 0) {
            mLoadingLayout.setStatus(LoadingLayout.Success);
        } else {
            mLoadingLayout.setStatus(LoadingLayout.Empty);
        }
        mAdapter.setNewData(cities);
    }

    public void showLoading() {
        mLoadingLayout.setStatus(LoadingLayout.Loading);
    }

    private void getCities() {
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
