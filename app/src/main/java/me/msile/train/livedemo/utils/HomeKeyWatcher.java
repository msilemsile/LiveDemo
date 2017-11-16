package me.msile.train.livedemo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import java.util.ArrayList;

import me.msile.train.livedemo.LiveDemoApplication;

/**
 * Home键监听封装
 */
public class HomeKeyWatcher {

    private IntentFilter mFilter;
    private ArrayList<OnHomeKeyWatcherListener> mListenerList = new ArrayList<>();
    private InnerReceiver mReceiver;
    private static HomeKeyWatcher instance;

    private HomeKeyWatcher() {
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    public static HomeKeyWatcher getInstance() {
        if (instance == null) {
            instance = new HomeKeyWatcher();
        }
        return instance;
    }

    // 回调接口
    public interface OnHomeKeyWatcherListener {
        void onHomePressed();

        void onHomeLongPressed();

        void onHomeKeyResume();
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void addOnHomePressedListener(OnHomeKeyWatcherListener listener) {
        if (mReceiver == null) {
            mReceiver = new InnerReceiver();
            startWatch();
        }
        if (listener != null) {
            mListenerList.add(listener);
        }
    }

    public void removeOnHomePressedListener(OnHomeKeyWatcherListener listener) {
        if (listener != null) {
            mListenerList.remove(listener);
        }
        if (mListenerList.isEmpty()) {
            stopWatch();
        }
    }

    public void handleHomeKeyResume() {
        if (mReceiver == null) {
            return;
        }
        for (OnHomeKeyWatcherListener homePressedListener : mListenerList) {
            homePressedListener.onHomeKeyResume();
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
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    for (OnHomeKeyWatcherListener homePressedListener : mListenerList) {
                        if (homePressedListener != null) {
                            if (TextUtils.equals(reason, SYSTEM_DIALOG_REASON_HOME_KEY)) {
                                // 短按home键
                                homePressedListener.onHomePressed();
                            } else if (TextUtils.equals(reason, SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                                // 长按home键
                                homePressedListener.onHomeLongPressed();
                            }
                        }
                    }
                }
            }
        }
    }
}