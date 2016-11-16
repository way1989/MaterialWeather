package com.ape.material.weather.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.ape.material.weather.R;


/**
 * This class is the default empty state view for most listviews/fragments
 * It allows the ability to set a main text, a main highlight text and a secondary text
 * By default this container has some strings loaded, but other classes can call the apis to change
 * the text
 */
public class LoadingEmptyContainer extends FrameLayout {
    private static final long LOADING_DELAY = 50L;

    public LoadingEmptyContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        hideAll();
    }

    public void hideAll() {
        findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
        getNoResultsContainer().setVisibility(View.INVISIBLE);
    }

    public void showLoading() {
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        findViewById(R.id.progressbar).animate().alpha(1f).setDuration(LOADING_DELAY);
        getNoResultsContainer().setVisibility(View.INVISIBLE);
    }

    public void showNoResults() {
        findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
        findViewById(R.id.progressbar).setAlpha(0f);
        getNoResultsContainer().setVisibility(View.VISIBLE);
    }

    public NoResultsContainer getNoResultsContainer() {
        return (NoResultsContainer) findViewById(R.id.no_results_container);
    }
}
