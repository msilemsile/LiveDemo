package me.msile.train.livedemo.utils;

import android.content.Context;


/**
 * 显示工具类
 */
public class DisplayUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕高度（像素）
     */
    public static int getDisplayHeightPixels(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(像素)
     */
    public static int getDisplayWidthPixels(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }


    // 获取状态栏高度
    public static int getStatusBarHeightSimple(Context context) {
        return (int) (25 * context.getResources().getDisplayMetrics().density);
    }

    // 获取屏幕内容高度(除去状态栏)
    public static int getContentHeight(Context context) {
        return getDisplayHeightPixels(context) - getStatusBarHeight(context);
    }

    /**
     * 获取状态栏高度(1.resource获取 2.反射获取 3.粗略计算)
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        if (result <= 0) {
            result = getStatusClassHeight(context);
        }
        if (result <= 0) {
            result = getStatusBarHeightSimple(context);
        }
        return result;
    }

    /**
     * 获得状态栏的高度
     */
    public static int getStatusClassHeight(Context context) {
        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

}
