package me.msile.train.livedemo;

import android.app.Application;

import me.msile.train.livedemo.manager.LiveFloatViewManager;
import me.msile.train.livedemo.utils.HomeKeyWatcher;
import me.msile.train.livedemo.utils.ScreenStatusWatcher;

/**
 * 直播demo
 */

public class LiveDemoApplication extends Application {

    private static LiveDemoApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static LiveDemoApplication getContext() {
        return instance;
    }

    public void appExit() {
        //移除直播组件
        LiveFloatViewManager.getInstance().removeLiveFloatView(true);
        //停止Home键监听
        HomeKeyWatcher.getInstance().stopWatch();
        //停止屏幕监听
        ScreenStatusWatcher.getInstance().stopWatch();
    }

}
