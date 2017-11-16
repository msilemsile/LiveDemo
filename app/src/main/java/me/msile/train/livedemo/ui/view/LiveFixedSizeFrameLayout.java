package me.msile.train.livedemo.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import me.msile.train.livedemo.utils.DisplayUtils;

/**
 * 直播固定大小布局
 */

public class LiveFixedSizeFrameLayout extends FrameLayout {

    private int mFixedWidth;
    private int mFixedHeight;

    public LiveFixedSizeFrameLayout(Context context) {
        super(context);
        init(context);
    }

    public LiveFixedSizeFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LiveFixedSizeFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mFixedWidth = DisplayUtils.getDisplayWidthPixels(context);
        mFixedHeight = DisplayUtils.getContentHeight(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newWidthSpec = MeasureSpec.makeMeasureSpec(mFixedWidth, MeasureSpec.EXACTLY);
        int newHeightSpec = MeasureSpec.makeMeasureSpec(mFixedHeight, MeasureSpec.EXACTLY);
        super.onMeasure(newWidthSpec, newHeightSpec);
    }
}
