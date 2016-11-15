package com.ape.material.weather.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ape.material.weather.R;
import com.ape.material.weather.base.BaseActivity;

import butterknife.BindView;

public class SearchCityActivity extends BaseActivity implements SearchView.OnQueryTextListener, View.OnTouchListener {
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
        hideInputManager();
        if (query.equals(queryString)) {
            return true;
        }
//        if (mSearchTask != null) {
//            mSearchTask.cancel(false);
//            mSearchTask = null;
//        }
//        queryString = query;
//        if (queryString.trim().equals("")) {
//            searchResults.clear();
//            adapter.updateSearchResults(searchResults);
//            adapter.notifyDataSetChanged();
//        } else {
//            mSearchTask = new SearchTask().executeOnExecutor(mSearchExecutor, queryString);
//        }
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

            //SearchHistory.getInstance(this).addSearchString(queryString);
        }
    }

}
