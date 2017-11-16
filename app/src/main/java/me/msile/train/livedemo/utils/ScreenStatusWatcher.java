package me.msile.train.livedemo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;

import me.msile.train.livedemo.LiveDemoApplication;

/**
 * 屏幕状态状态监听监听封装
 */
public class ScreenStatusWatcher {

    private IntentFilter mFilter;
    private ArrayList<OnScreenStatusWatcherListener> mListenerList = new ArrayList<>();
    private InnerReceiver mReceiver;
    private static ScreenStatusWatcher instance;

    private ScreenStatusWatcher() {
        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_SCREEN_ON);
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mFilter.addAction(Intent.ACTION_USER_PRESENT);
    }

    public static ScreenStatusWatcher getInstance() {
        if (instance == null) {
            instance = new ScreenStatusWatcher();
        }
        return instance;
    }

    // 回调接口
    public interface OnScreenStatusWatcherListener {
        void onScreenOn();

        void onScreenOff();

        void onScreenOpen();
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void addOnScreenStatusListener(OnScreenStatusWatcherListener listener) {
        if (mReceiver == null) {
            mReceiver = new InnerReceiver();
            startWatch();
        }
        if (listener != null) {
            mListenerList.add(listener);
        }
    }

    public void removeOnScreenStatusListener(OnScreenStatusWatcherListener listener) {
        if (listener != null) {
            mListenerList.remove(listener);
        }
        if (mListenerList.isEmpty()) {
            stopWatch();
        }
    }

    /**
     * 开始监听，注册广播
     */
    private void startWatch() {
        Context context = LiveDemoApplication.getContext();
        if (mReceiver != null && context != null) {
            context.registerReceiver(mReceiver, mFilter);
        }
    }

    /**
     * 停止监听，注销广播
     */
    public void stopWatch() {
        Context context = LiveDemoApplication.getContext();
        if (mReceiver != null && context != null) {
            context.unregisterReceiver(mReceiver);
            mListenerList.clear();
            mReceiver = null;
        }
    }

    /**
     * 广播接收者
     */
    class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                for (OnScreenStatusWatcherListener listener : mListenerList) {
                    listener.onScreenOn();
                }
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                for (OnScreenStatusWatcherListener listener : mListenerList) {
                    listener.onScreenOff();
                }
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                for (OnScreenStatusWatcherListener listener : mListenerList) {
                    listener.onScreenOpen();
                }
            }
        }
    }
}