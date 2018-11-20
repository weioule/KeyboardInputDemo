package com.example.weioule.inputkeyboarddemo;

import android.app.Application;

/**
 * Author by weioule.
 * Date on 2018/11/19.
 */
public class MyApplication extends Application {
    protected static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication instance() {
        return instance;
    }

    public String getCachePath() {
        return "/Android/data/" + getPackageName() + "/cache/";
    }

    public String getCacheForeverPath() {
        return "/Android/data/" + getPackageName() + "/cache_forever/";
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
