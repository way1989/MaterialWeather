package com.ape.material.weather.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.ape.material.weather.R;
import com.ape.material.weather.base.BaseActivity;
import com.ape.material.weather.bean.City;
import com.ape.material.weather.db.SearchHistory;
import com.ape.material.weather.util.AppConstant;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;

import java.util.List;

import butterknife.BindView;

public class SearchCityActivity extends BaseActivity<SearchPresenter, SearchModel> implements SearchContract.View,
        SearchView.OnQueryTextListener, View.OnTouchListener, SearchAdapter.OnItemClickListener {
    private static final String TAG = "SearchCityActivity";
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
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
    protected void initPresenter() {
        super.initPresenter();
        mPresenter.setVM(this, mModel);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_city;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.search_city));

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setIconified(false);

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.menu_search), new MenuItemCompat.OnActionExpandListener() {
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
        if (query.equals(queryString)) {
            return true;
        }
        queryString = query;
        if (queryString.trim().equals("")) {
            adapter.clear();
        } else {
            mPresenter.search(queryString);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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
            SearchHistory.getInstance(this).addSearchString(queryString);
        }
    }

    @Override
    public void onSearchResult(List<City> cities) {
        Log.d(TAG, "onSearchResult... city size = " + cities.size());
        if (cities.isEmpty()) {
            adapter.clear();
        } else {
            adapter.updateSearchResults(cities);
        }
    }

    @Override
    public void onSearchError(Throwable e) {
        Log.d(TAG, "onSearchError... e = " + e.getMessage());
        adapter.clear();
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveCitySucceed(City city) {
        Log.d(TAG, "onSaveCitySucceed city");
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
