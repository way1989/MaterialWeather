package com.ape.material.weather.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.MenuItem;

import com.ape.material.weather.App;
import com.ape.material.weather.AppComponent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by android on 16-11-10.
 */

public abstract class BaseActivity<T extends BasePresenter> extends RxAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initPresenter(((App) getApplication()).getAppComponent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * should override this method when use MVP
     */
    protected void initPresenter(AppComponent appComponent) {
    }

    /**
     * must override this method
     *
     * @return resource layout id
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

}
