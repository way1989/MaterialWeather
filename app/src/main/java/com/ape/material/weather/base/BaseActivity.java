package com.ape.material.weather.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ape.material.weather.util.TUtil;

import butterknife.ButterKnife;

/**
 * Created by android on 16-11-10.
 */

public abstract class BaseActivity<T extends BasePresenter, E extends BaseModel> extends AppCompatActivity {
    protected T mPresenter;
    protected E mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        mPresenter = TUtil.getT(this, 0);
        mModel = TUtil.getT(this, 1);
        initPresenter();
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
        if (mPresenter != null)
            mPresenter.onDestroy();
    }

    /**
     * should override this method when use MVP
     */
    protected void initPresenter() {
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
