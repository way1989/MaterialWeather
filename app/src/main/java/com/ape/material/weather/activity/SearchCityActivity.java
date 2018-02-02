package com.ape.material.weather.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.R;
import com.ape.material.weather.adapter.SearchAdapter;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.util.AppConstant;
import com.ape.material.weather.util.RxSchedulers;
import com.ape.material.weather.widget.SimpleListDividerDecorator;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.weavey.loading.lib.LoadingLayout;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class SearchCityActivity extends BaseActivity implements MenuItem.OnActionExpandListener, View.OnTouchListener {
    private static final String TAG = "SearchCityActivity";
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.loading_layout)
    LoadingLayout mLoadingLayout;
    private SearchAdapter mSearchAdapter;
    private SearchView mSearchView;
    private InputMethodManager mInputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchAdapter = new SearchAdapter(R.layout.item_search_city);
        mSearchAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        mSearchAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final City city = mSearchAdapter.getItem(position);
                SearchCityActivity.this.onItemClick(city);
            }
        });
        mRecyclerView.setAdapter(mSearchAdapter);
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat
                .getDrawable(getApplicationContext(), R.drawable.list_divider_h), false));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_city;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.search_city));
        //mSearchView.setIconifiedByDefault(false);
        //mSearchView.setIconified(false);
        searchMenuItem.expandActionView();

        searchMenuItem.setOnActionExpandListener(this);

        RxSearchView.queryTextChanges(mSearchView)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        search(charSequence.toString());
                    }
                });
        return super.onCreateOptionsMenu(menu);
    }


    private void search(String query) {
        if (TextUtils.isEmpty(query) || query.trim().equals("")) {
            mLoadingLayout.setStatus(LoadingLayout.Empty);
            mSearchAdapter.setNewData(null);
        } else {
            mLoadingLayout.setStatus(LoadingLayout.Loading);
            mViewModel.search(query)
                    .compose(this.<List<City>>bindUntilEvent(ActivityEvent.DESTROY))
                    .compose(RxSchedulers.<List<City>>io_main())
                    .subscribe(new Consumer<List<City>>() {
                        @Override
                        public void accept(List<City> cities) throws Exception {
                            onSearchResult(cities);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e(TAG, "accept: search city ", throwable);
                            onSearchError(throwable);
                        }
                    });
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hideInputManager();
        return false;
    }

    public void hideInputManager() {
        if (mSearchView != null) {
            if (mInputMethodManager != null) {
                mInputMethodManager.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
            }
            mSearchView.clearFocus();
        }
    }

    public void onSearchResult(List<City> cities) {
        Log.d(TAG, "onSearchResult... city size = " + cities.size());
        if (cities.isEmpty()) {
            mSearchAdapter.setNewData(null);
            mLoadingLayout.setStatus(LoadingLayout.Empty);
        } else {
            mSearchAdapter.setNewData(cities);
            mLoadingLayout.setStatus(LoadingLayout.Success);
        }
    }

    public void onSearchError(Throwable e) {
        Log.d(TAG, "onSearchError... e = " + e.getMessage());
        mSearchAdapter.setNewData(null);
        mLoadingLayout.setStatus(LoadingLayout.Empty);
        if (BuildConfig.LOG_DEBUG)
            Snackbar.make(mRecyclerView, e.getMessage(), Snackbar.LENGTH_LONG).show();
    }

    public void onSaveCitySucceed(City city) {
        Log.d(TAG, "onSaveCitySucceed city = " + city);
        if (city == null) {
            Snackbar.make(mRecyclerView, R.string.city_exist, Snackbar.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(AppConstant.ARG_CITY_KEY, city);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onItemClick(final City city) {
        Log.d(TAG, "onItemClick city = " + city.getCity());
        hideInputManager();
        mViewModel.addOrUpdateCity(city)
                .compose(this.<Boolean>bindUntilEvent(ActivityEvent.DESTROY))
                .compose(RxSchedulers.<Boolean>io_main())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        onSaveCitySucceed(result ? city : null);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "accept: addOrUpdateCity city = " + city, throwable);
                    }
                });
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        finish();
        return true;
    }
}
