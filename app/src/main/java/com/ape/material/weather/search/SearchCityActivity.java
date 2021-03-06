package com.ape.material.weather.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ape.material.weather.AppComponent;
import com.ape.material.weather.BuildConfig;
import com.ape.material.weather.R;
import com.ape.material.weather.base.BaseActivity;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.util.AppConstant;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.weavey.loading.lib.LoadingLayout;

import java.util.List;

import butterknife.BindView;

public class SearchCityActivity extends BaseActivity<SearchPresenter> implements SearchContract.View,
        SearchView.OnQueryTextListener, View.OnTouchListener, SearchAdapter.OnItemClickListener {
    private static final String TAG = "SearchCityActivity";
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.loading_layout)
    LoadingLayout mLoadingLayout;
    private SearchAdapter adapter;
    private SearchView mSearchView;
    private InputMethodManager mImm;
    private String queryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(this);
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.addItemDecoration(new SimpleListDividerDecorator(ContextCompat
                .getDrawable(getApplicationContext(), R.drawable.list_divider_h), true));
    }

    @Override
    protected void initPresenter(AppComponent appComponent) {
        super.initPresenter(appComponent);
        DaggerSearchComponent.builder().appComponent(appComponent).searchPresenterModule(new SearchPresenterModule(this)).build().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_city;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SupportMenuItem searchMenuItem = (SupportMenuItem) menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.search_city));

        //mSearchView.setIconifiedByDefault(false);
        //mSearchView.setIconified(false);

        MenuItemCompat.setOnActionExpandListener(searchMenuItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        finish();
                        return false;
                    }
                });

        menu.findItem(R.id.menu_search).expandActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit... query = " + query);
        hideInputManager();
        search(query);
        return true;
    }

    private void search(String query) {
        if (query.equals(queryString)) {
            return;
        }
        queryString = query;
        if (queryString.trim().equals("")) {
            adapter.clear();
            mLoadingLayout.setStatus(LoadingLayout.Empty);
        } else {
            mPresenter.search(queryString);
            mLoadingLayout.setStatus(LoadingLayout.Loading);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hideInputManager();
        return false;
    }

    public void hideInputManager() {
        if (mSearchView != null) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
            }
            mSearchView.clearFocus();
        }
    }

    @Override
    public void onSearchResult(List<City> cities) {
        Log.d(TAG, "onSearchResult... city size = " + cities.size());
        if (cities.isEmpty()) {
            adapter.clear();
            mLoadingLayout.setStatus(LoadingLayout.Empty);
        } else {
            adapter.updateSearchResults(cities);
            mLoadingLayout.setStatus(LoadingLayout.Success);
        }
    }

    @Override
    public void onSearchError(Throwable e) {
        Log.d(TAG, "onSearchError... e = " + e.getMessage());
        adapter.clear();
        mLoadingLayout.setStatus(LoadingLayout.Empty);
        if (BuildConfig.LOG_DEBUG)
            Snackbar.make(mRecyclerview, e.getMessage(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onSaveCitySucceed(City city) {
        Log.d(TAG, "onSaveCitySucceed city");
        if (city == null) {
            Snackbar.make(mRecyclerview, R.string.city_exist, Snackbar.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(AppConstant.ARG_CITY_KEY, city);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemClick(City city) {
        Log.d(TAG, "onItemClick city = " + city.getCity());
        mPresenter.addOrUpdateCity(city);
    }
}
