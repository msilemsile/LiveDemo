package me.msile.train.livedemo.ui.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import me.msile.train.livedemo.utils.DisplayUtils;
import me.msile.train.livedemo.utils.HomeKeyWatcher;
import me.msile.train.livedemo.utils.ScreenStatusWatcher;

/**
 * 应用浮动窗体(使用type_toast 避开悬浮窗权限)
 */

public class LiveFloatViewHelper implements View.OnTouchListener, HomeKeyWatcher.OnHomeKeyWatcherListener, ScreenStatusWatcher.OnScreenStatusWatcherListener {

    private WindowManager.LayoutParams mWindowLayoutParams;
    private WindowManager mWindowManager;
    private LiveVideoView mContentView;
    private LiveFloatLoadingView mLoadingView;
    private int mStatusHeight;
    private int mScreenWidth;

    private float mTouchStartX;
    private float mTouchStartY;
    private float mTouchMoveX;
    private float mTouchMoveY;
    private float x;
    private float y;

    private boolean isTouchClick;
    private int slop;
    private FloatOnClickListener floatOnClickListener;
    private boolean isHomeClick;

    private Context mContext;

    private LiveFloatViewHelper() {
    }

    public LiveFloatViewHelper(Context context) {
        mContext = context;
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT;
        mWindowLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        int windowType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //解决Android 7.1.1起不能再用Toast的问题（先解决crash）
            if (Build.VERSION.SDK_INT > 24) {
                windowType = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                windowType = WindowManager.LayoutParams.TYPE_TOAST;
            }
        } else {
            windowType = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mWindowLayoutParams.type = windowType;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = DisplayUtils.getStatusBarHeight(context);
        mScreenWidth = DisplayUtils.getDisplayWidthPixels(context);
        slop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * 设置浮动布局
     */
    public void setContentView(LiveVideoView contentView) {
        if (mWindowManager != null) {
            if (hasContentView()) {
                mWindowManager.removeView(mContentView);
                HomeKeyWatcher.getInstance().removeOnHomePressedListener(this);
                ScreenStatusWatcher.getInstance().removeOnScreenStatusListener(this);
                mContentView = null;
            }
            if (contentView != null) {
                mContentView = contentView;
                mWindowLayoutParams.width = mContentView.getFloatWidth();
                mWindowLayoutParams.height = mContentView.getFloatHeight();
                mWindowLayoutParams.x = mContentView.getStartXPos();
                mWindowLayoutParams.y = mContentView.getStartYPos();
                mWindowManager.addView(contentView, mWindowLayoutParams);
                mContentView.setOnTouchListener(this);
                HomeKeyWatcher.getInstance().addOnHomePressedListener(this);
                ScreenStatusWatcher.getInstance().addOnScreenStatusListener(this);
            }
        }
    }

    /**
     * 移除浮动布局
     */
    public void removeContentView(boolean releasePlayer) {
        if (mWindowManager != null) {
            if (hasContentView()) {
                if (releasePlayer) {
                    mContentView.stopLiveVideo();
                }
                mContentView.setOnTouchListener(null);
                mWindowManager.removeView(mContentView);
                mContentView = null;
                HomeKeyWatcher.getInstance().removeOnHomePressedListener(this);
                ScreenStatusWatcher.getInstance().removeOnScreenStatusListener(this);
            }
        }
    }

    /**
     * 移除播放器
     */
    public void removeContentViewWithLoading() {
        if (mWindowManager != null) {
            if (hasContentView()) {
                mContentView.stopLiveVideo();
                mContentView.setOnTouchListener(null);
                mWindowManager.removeView(mContentView);
                mContentView = null;
            }
            if (mLoadingView == null) {
                mLoadingView = new LiveFloatLoadingView(mContext);
            }
            mWindowManager.addView(mLoadingView, mWindowLayoutParams);
        }
    }

    /***
     * 是否有浮动窗体
     */
    public boolean hasContentView() {
        return mContentView != null && mContentView.getParent() != null;
    }

    @Override
    public void onHomePressed() {
        handleHomeKeyPause();
    }

    @Override
    public void onHomeLongPressed() {
//        handleHomeKeyPause();
    }

    @Override
    public void onHomeKeyResume() {
        handleHomeKeyResume();
    }

    @Override
    public void onScreenOn() {

    }

    @Override
    public void onScreenOff() {
        if (hasContentView()) {
            mContentView.pauseLiveVideo();
        }
    }

    @Override
    public void onScreenOpen() {
        if (hasContentView()) {
            mContentView.startLiveVideo();
        }
    }

    /**
     * 暂停播放 并且移除
     */
    private void handleHomeKeyPause() {
        if (!isHomeClick && hasContentView()) {
            isHomeClick = true;
        }
    }

    /**
     * home键恢复播放 时间有点长
     */
    private void handleHomeKeyResume() {
        if (isHomeClick && isTouchClick) {
            isTouchClick = false;
            isHomeClick = false;
            if (mLoadingView != null && mLoadingView.getParent() != null) {
                mWindowManager.removeView(mLoadingView);
                mLoadingView = null;
            }
        }
        isHomeClick = false;
        isTouchClick = false;
    }

    public boolean isHomeClick() {
        return isHomeClick;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!hasContentView()) {
            return false;
        }
        x = event.getRawX();
        y = event.getRawY() - mStatusHeight;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                mTouchMoveX = x;
                mTouchMoveY = y;
                isTouchClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - mTouchMoveX) > slop || Math.abs(y - mTouchMoveY) > slop) {
                    isTouchClick = false;
                }
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                if (isTouchClick && floatOnClickListener != null && mContentView != null && mContentView.getParent() != null) {
                    floatOnClickListener.onFloatClick();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                isTouchClick = false;
//                resetViewPosition();
                break;
        }
        return true;
    }

    /**
     * 悬浮view归位
     */
    private void resetViewPosition() {
        int centerXPos = mScreenWidth / 2;
        if (mWindowLayoutParams.x >= centerXPos - mContentView.getWidth() / 2) {
            mWindowLayoutParams.x = mScreenWidth - mContentView.getWidth();
        } else {
            mWindowLayoutParams.x = 0;
        }
        mWindowManager.updateViewLayout(mContentView, mWindowLayoutParams);
    }

    /**
     * 更新悬浮view位置
     */
    private void updateViewPosition() {
        if (hasContentView()) {
            mWindowLayoutParams.x = (int) (x - mTouchStartX);
            mWindowLayoutParams.y = (int) (y - mTouchStartY);
            mWindowManager.updateViewLayout(mContentView, mWindowLayoutParams);
        }
    }

    public void setFloatOnClickListener(FloatOnClickListener floatOnClickListener) {
        this.floatOnClickListener = floatOnClickListener;
    }

    public interface FloatOnClickListener {
        void onFloatClick();
    }

}