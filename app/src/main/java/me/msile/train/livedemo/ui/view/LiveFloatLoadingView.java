package me.msile.train.livedemo.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import me.msile.train.livedemo.utils.DisplayUtils;

/**
 * 直播浮动progressBar
 */

public class LiveFloatLoadingView extends MaterialProgressBar {

    public LiveFloatLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveFloatLoadingView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setBarColor(Color.parseColor("#FF5073"));
        setBarWidth(8);
        setCircleRadius(DisplayUtils.dip2px(getContext(), 25));
        spin();
    }

}
