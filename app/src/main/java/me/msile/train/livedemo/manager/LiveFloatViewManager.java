package me.msile.train.livedemo.manager;

import android.content.Intent;
import android.view.ViewGroup;
import android.view.ViewParent;

import me.msile.train.livedemo.LiveDemoApplication;
import me.msile.train.livedemo.ui.activity.LiveRoomActivity;
import me.msile.train.livedemo.ui.view.LiveFloatViewHelper;
import me.msile.train.livedemo.ui.view.LiveVideoView;

/**
 * 直播管理
 */

public class LiveFloatViewManager {

    private static LiveFloatViewManager instance;
    private LiveFloatViewHelper mFloatViewHelper;
    private LiveVideoView mLiveVideoView;

    private LiveFloatViewManager() {
    }

    public static LiveFloatViewManager getInstance() {
        if (instance == null) {
            instance = new LiveFloatViewManager();
        }
        return instance;
    }

    /**
     * 添加直播悬浮窗
     *
     * @param liveAddress 直播地址
     */
    public void addLiveFloatView(String liveAddress) {
        if (mFloatViewHelper == null) {
            mFloatViewHelper = new LiveFloatViewHelper(LiveDemoApplication.getContext());
            mFloatViewHelper.setFloatOnClickListener(new LiveFloatViewHelper.FloatOnClickListener() {
                @Override
                public void onFloatClick() {
                    removeLiveFloatView(false);
                    Intent intent = new Intent(LiveDemoApplication.getContext(), LiveRoomActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    LiveDemoApplication.getContext().startActivity(intent);
                }
            });
        }
        LiveVideoView liveVideoView = getLiveVideoView(1);
        liveVideoView.setVideoUrl(liveAddress, new LiveVideoView.LiveFloatCloseListener() {
            @Override
            public void onClickClose() {
                removeLiveFloatView(true);
                clearLastLiveRoomData();
            }
        });
        mFloatViewHelper.setContentView(liveVideoView);
    }

    /**
     * 移除直播悬浮窗
     */
    public void removeLiveFloatView(boolean releasePlayer) {
        if (mFloatViewHelper == null) {
            return;
        }
        if (mFloatViewHelper.isHomeClick() && !releasePlayer) {
            mFloatViewHelper.removeContentViewWithLoading();
        } else {
            mFloatViewHelper.removeContentView(releasePlayer);
        }
        if (releasePlayer) {
            mFloatViewHelper = null;
        }
    }

    /**
     * 是否有直播浮动窗体
     */
    public boolean hasLiveFloatView() {
        return mFloatViewHelper != null && mFloatViewHelper.hasContentView();
    }

    /**
     * 移除上一个直播间数据(播放器、直播间id)
     */
    public void clearLastLiveRoomData() {
        if (!hasLiveFloatView()) {
            removeLiveVideoFromParent();
            if (mLiveVideoView != null) {
                mLiveVideoView.stopLiveVideo();
            }
            mLiveVideoView = null;
        }
    }

    /**
     * 从父布局移除VideoView(假如)
     */
    private void removeLiveVideoFromParent() {
        if (mLiveVideoView != null) {
            ViewParent mParent = mLiveVideoView.getParent();
            if (mParent instanceof ViewGroup) {
                ((ViewGroup) mParent).removeView(mLiveVideoView);
            }
        }
    }

    /**
     * 获取单独的播放器
     *
     * @param type 0 正常模式 1 小窗模式
     */
    public LiveVideoView getLiveVideoView(int type) {
        removeLiveVideoFromParent();
        if (mLiveVideoView == null) {
            mLiveVideoView = new LiveVideoView(LiveDemoApplication.getContext());
        } else if (mLiveVideoView.isLiveVideoRelease()) {
            mLiveVideoView.stopLiveVideo();
            mLiveVideoView = null;
            mLiveVideoView = new LiveVideoView(LiveDemoApplication.getContext());
        }
        if (type == 0) {
            mLiveVideoView.setNormalMode();
        } else if (type == 1) {
            mLiveVideoView.setFloatMode();
        }
        return mLiveVideoView;
    }

}
