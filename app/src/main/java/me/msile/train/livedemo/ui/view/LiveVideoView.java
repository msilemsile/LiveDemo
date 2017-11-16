package me.msile.train.livedemo.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PlayerState;
import com.pili.pldroid.player.widget.PLVideoView;

import me.msile.train.livedemo.utils.DisplayUtils;
import me.msile.train.livedemo.R;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 直播播放器
 */

public class LiveVideoView extends FrameLayout {

    private PLVideoView mPLVideoView;
    private ProgressBar mProgressBar;
    private ImageView mLoadingBg;
    private ImageView mCloseView;

    private int mFloatWidth;
    private int mFloatHeight;

    private int startXPos;
    private int startYPos;

    private String mVideoUrl;

    private LiveFloatCloseListener mCloseListener;

    public LiveVideoView(Context context) {
        super(context);
        init(context);
    }

    public LiveVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LiveVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mFloatWidth = DisplayUtils.dip2px(context, 123);
        mFloatHeight = DisplayUtils.dip2px(context, 220);
        startXPos = DisplayUtils.getDisplayWidthPixels(context) - mFloatWidth;
        startYPos = DisplayUtils.getContentHeight(context) / 2 - mFloatHeight;
        //保持屏幕长亮
        setKeepScreenOn(true);

        //添加播放器
        mPLVideoView = new PLVideoView(context);
        initPlayerOptions();
        addView(mPLVideoView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        //添加背景
        mLoadingBg = new ImageView(context);
        mLoadingBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mLoadingBg.setImageResource(R.mipmap.live_loading_bg);
        addView(mLoadingBg, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        //添加loading
        mProgressBar = new ProgressBar(context);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.player_loading_rotate_bg));
        int proWidth = DisplayUtils.dip2px(context, 20);
        FrameLayout.LayoutParams proParams = new FrameLayout.LayoutParams(proWidth, proWidth);
        proParams.gravity = Gravity.CENTER;
        addView(mProgressBar, proParams);

        //添加关闭按钮
        mCloseView = new ImageView(context);
        mCloseView.setImageResource(R.mipmap.live_close_icon);
        int closeWidth = DisplayUtils.dip2px(context, 35);
        int closePadding = DisplayUtils.dip2px(context, 10);
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(closeWidth, closeWidth);
        closeParams.gravity = Gravity.RIGHT;
        mCloseView.setPadding(closePadding, closePadding, closePadding, closePadding);
        mCloseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCloseListener != null) {
                    mCloseListener.onClickClose();
                }
            }
        });
        addView(mCloseView, closeParams);

        mPLVideoView.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer plMediaPlayer) {
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(GONE);
                }
                if (mLoadingBg != null) {
                    mLoadingBg.setVisibility(GONE);
                }
            }
        });
        mPLVideoView.setOnErrorListener(new PLMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(PLMediaPlayer plMediaPlayer, int i) {
                Log.i("-live_video_info-", "onError --info==" + i);
                switch (i) {
                    case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                        showToastMsg("连接失败!");
                        break;
                    case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                        showToastMsg("连接失败!");
                        break;
                    case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                        showToastMsg("连接超时!");
                        break;
                    case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                        showToastMsg("连接失败!");
                        break;
                    case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                        showToastMsg("连接失败!");
                        break;
                    case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                        showToastMsg("连接超时!");
                        break;
                    case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                        showToastMsg("连接超时!");
                        break;
                    case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                        showToastMsg("连接已断开!");
                        break;
                }
                return true;
            }
        });
        mPLVideoView.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(PLMediaPlayer plMediaPlayer, int i, int i1) {
                Log.i("-live_video_info-", "onInfo info==" + i);
                switch (i) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(View.VISIBLE);
                        }
                        if (mLoadingBg != null) {
                            mLoadingBg.setVisibility(GONE);
                        }
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(View.GONE);
                        }
                        if (mLoadingBg != null) {
                            mLoadingBg.setVisibility(GONE);
                        }
                        break;
                }
                return false;
            }
        });
        mPLVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        mPLVideoView.setScreenOnWhilePlaying(true);
    }

    private void showToastMsg(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 设置播放地址
     */
    public void setVideoUrl(String videoUrl, LiveFloatCloseListener closeListener) {
        if (TextUtils.isEmpty(videoUrl)) {
            return;
        }

        this.mCloseListener = closeListener;
        if (TextUtils.equals(mVideoUrl, videoUrl)) {
            if (isLiveVideoPause()) {
                startLiveVideo();
            }
        } else {
            mPLVideoView.setVideoPath(videoUrl);
        }
        mVideoUrl = videoUrl;
    }

    /**
     * 设置浮动模式
     */
    public void setFloatMode() {
        mCloseView.setVisibility(VISIBLE);
        if (isLiveVideoRelease()) {
            mProgressBar.setVisibility(VISIBLE);
            mLoadingBg.setVisibility(VISIBLE);
        }
    }

    /**
     * 设置普通模式
     */
    public void setNormalMode() {
        mCloseView.setVisibility(GONE);
        if (isLiveVideoRelease()) {
            mProgressBar.setVisibility(VISIBLE);
            mLoadingBg.setVisibility(VISIBLE);
        }
    }

    /**
     * 初始化播放器配置
     */
    private void initPlayerOptions() {
        AVOptions options = new AVOptions();
        // 解码方式:
        // codec＝AVOptions.MEDIA_CODEC_HW_DECODE，硬解
        // codec=AVOptions.MEDIA_CODEC_SW_DECODE, 软解
        // codec=AVOptions.MEDIA_CODEC_AUTO, 硬解优先，失败后自动切换到软解
        // 默认值是：MEDIA_CODEC_SW_DECODE
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_AUTO);
        // 准备超时时间，包括创建资源、建立连接、请求码流等，单位是 ms
        // 默认值是：无
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 60 * 1000);
        // 读取视频流超时时间，单位是 ms
        // 默认值是：10 * 1000
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 60 * 1000);
        // 当前播放的是否为在线直播，如果是，则底层会有一些播放优化
        // 默认值是：0
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        // 是否开启"延时优化"，只在在线直播流中有效
        // 默认值是：0
        options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        // 默认的缓存大小，单位是 ms
        // 默认值是：2000
        options.setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 4000);
        // 最大的缓存大小，单位是 ms
        // 默认值是：4000
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 6000);
        // 是否自动启动播放，如果设置为 1，则在调用 `prepareAsync` 或者 `setVideoPath` 之后自动启动播放，无需调用 `start()`
        // 默认值是：1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 1);
        // 播放前最大探测流的字节数，单位是 byte
        // 默认值是：128 * 1024
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        // 设置重新连接
        options.setInteger(AVOptions.KEY_RECONNECT, 1);
        // 请在开始播放之前配置
        mPLVideoView.setAVOptions(options);
    }

    public void stopLiveVideo() {
        if (mPLVideoView != null) {
            mPLVideoView.stopPlayback();
        }
    }

    public void pauseLiveVideo() {
        if (mPLVideoView != null) {
            mPLVideoView.pause();
        }
    }

    public void startLiveVideo() {
        if (mPLVideoView != null) {
            mPLVideoView.start();
        }
    }

    public boolean isLiveVideoPlaying() {
        return mPLVideoView != null && mPLVideoView.getPlayerState() == PlayerState.PLAYING;
    }

    public boolean isLiveVideoPause() {
        return mPLVideoView != null && mPLVideoView.getPlayerState() == PlayerState.PAUSED;
    }

    public boolean hasLiveVideo() {
        return !TextUtils.isEmpty(mVideoUrl) && (isLiveVideoPause() || isLiveVideoPlaying());
    }

    public boolean isLiveVideoRelease() {
        return mPLVideoView != null && (mPLVideoView.getPlayerState() == PlayerState.IDLE || mPLVideoView.getPlayerState() == PlayerState.ERROR);
    }

    public int getStartXPos() {
        return startXPos;
    }

    public int getStartYPos() {
        return startYPos;
    }

    public int getFloatWidth() {
        return mFloatWidth;
    }

    public int getFloatHeight() {
        return mFloatHeight;
    }

    public interface LiveFloatCloseListener {
        void onClickClose();
    }

}
