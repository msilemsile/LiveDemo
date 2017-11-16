package me.msile.train.livedemo.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.msile.train.livedemo.utils.DisplayUtils;

/**
 * 直播滑动view
 */

public class LiveTransOffsetView extends View {

    private int mFixedWidth;
    private int mFixedHeight;
    private float mDownX;
    private float mTransOffset;
    private int mScreenWidth;
    private boolean isShowScreen = true;
    private boolean isAnimEnd = true;

    private List<View> mTransViews = new ArrayList<>();

    public LiveTransOffsetView(Context context) {
        super(context);
        init(context);
    }

    public LiveTransOffsetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LiveTransOffsetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mFixedWidth = DisplayUtils.getDisplayWidthPixels(context);
        mFixedHeight = DisplayUtils.getContentHeight(context);
        mScreenWidth = DisplayUtils.getDisplayWidthPixels(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newWidthSpec = MeasureSpec.makeMeasureSpec(mFixedWidth, MeasureSpec.EXACTLY);
        int newHeightSpec = MeasureSpec.makeMeasureSpec(mFixedHeight, MeasureSpec.EXACTLY);
        super.onMeasure(newWidthSpec, newHeightSpec);
    }

    public void addTransView(View transView) {
        if (transView != null) {
            mTransViews.add(transView);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTransViews.isEmpty() || !isAnimEnd) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float offsetX = event.getX() - mDownX;
                mTransOffset += offsetX;
                if (mTransOffset <= 0) {
                    mTransOffset = 0;
                }
                updatePosition();
                mDownX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                resetPosition();
                break;
        }
        return true;
    }

    private void resetPosition() {
        int realScrollOffset;
        if (isShowScreen) {
            realScrollOffset = (int) mTransOffset;
        } else {
            realScrollOffset = (int) (mTransOffset - mScreenWidth);
        }

        if (Math.abs(realScrollOffset) > 80) {
            isShowScreen = !isShowScreen;
        }
        isAnimEnd = false;
        if (isShowScreen) {
            mTransOffset = 0;
        } else {
            mTransOffset = mScreenWidth;
        }
        for (View view : mTransViews) {
            view.animate().translationX(mTransOffset).setDuration(300).withEndAction(new Runnable() {
                @Override
                public void run() {
                    isAnimEnd = true;
                }
            });
        }
    }

    private void updatePosition() {
        for (View view : mTransViews) {
            view.setTranslationX(mTransOffset);
        }
    }

}
