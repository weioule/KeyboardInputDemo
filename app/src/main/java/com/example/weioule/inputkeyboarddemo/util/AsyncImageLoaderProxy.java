package com.example.weioule.inputkeyboarddemo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;


import com.example.weioule.inputkeyboarddemo.MyApplication;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author by weioule.
 * Date on 2018/11/19.
 */
public class AsyncImageLoaderProxy {
    // 软引用内存缓存
    private static Map<String, SoftReference<Bitmap>> sImageCache;
    // 图片三种获取方式管理者，网络URL获取、内存缓存获取、外部文件缓存获取
    private static LoaderImpl impl;
    // 图片获取完成通知UI线程
    private Handler handler;
    // 线程池相关
    private static ExecutorService sExecutorService;

    /**
     * 异步加载图片完毕的回调接口
     */
    public interface ImageCallback {
        /**
         * 回调函数
         *
         * @param bitmap   : may be null!
         * @param imageUrl
         */
        void onImageLoaded(Bitmap bitmap, String imageUrl);
    }

    static {
        sImageCache = new HashMap<>();
        impl = new LoaderImpl(sImageCache);
    }

    public AsyncImageLoaderProxy(Context context) {
        handler = new Handler();
        startThreadPoolIfNecessary();

        String defaultDir = context.getCacheDir().getAbsolutePath();
        setCachedDir(defaultDir);
    }

    /**
     * 是否缓存图片至文件系统  默认不缓存
     */
    public void setCache2File(boolean flag) {
        impl.setCache2File(flag);
    }

    /**
     * 设置缓存路径，setCache2File(true)时有效
     */
    public void setCachedDir(String dir) {
        impl.setCachedDir(dir);
    }

    /**
     * 开启线程池
     */
    public static void startThreadPoolIfNecessary() {
        if (sExecutorService == null || sExecutorService.isShutdown()
                || sExecutorService.isTerminated()) {
            sExecutorService = Executors.newFixedThreadPool(3);
        }
    }

    /**
     * 异步下载图片，并缓存到memory中
     *
     * @param url
     * @param callback see ImageCallback interface
     */
    public void downloadImage(final String url, final ImageCallback callback) {
        downloadImage(url, true, callback);
    }

    /**
     * 缓存文件到指定路径；当缓存超过5M时，将清空缓存
     */
    public void downloadCache2Sd(final String url, final ImageCallback callback) {
        impl.setCachedDir(Environment.getExternalStorageDirectory() + MyApplication.instance().getCachePath());
        impl.setCache2File(true);
        downloadImage(url, true, callback);
    }

    /**
     * 缓存文件到指定路径；程序不会主动清除缓存
     */
    public void downloadCacheForever(final String url, final ImageCallback callback) {
        impl.setCachedDir(Environment.getExternalStorageDirectory() + MyApplication.instance().getCacheForeverPath());
        impl.setCache2File(true);
        downloadImage(url, true, callback);
    }

    /**
     * 图片缓存到内存，只做soft缓存
     */
    public void downloadCache2memory(final String url, final ImageCallback callback) {
        impl.setCache2File(false);
        downloadImage(url, true, callback);
    }

    /**
     * @param url
     * @param cache2Memory 是否缓存至memory中
     * @param callback
     */
    public void downloadImage(final String url, final boolean cache2Memory,
                              final ImageCallback callback) {
        Bitmap bitmap = impl.getBitmapFromMemory(url);
        if (bitmap != null) {
            if (callback != null) {
                callback.onImageLoaded(bitmap, url);
            }
        } else {
            // 从网络端下载图片
            sExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    synchronized (sExecutorService) {
                        final Bitmap bitmap = impl.getBitmapFromUrl(url,
                                cache2Memory);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onImageLoaded(bitmap, url);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 预加载下一张图片，缓存至memory中
     *
     * @param url
     */
    public void preLoadNextImage(final String url) {
        // 将callback置为空，只将bitmap缓存到memory即可。
        downloadImage(url, null);
    }
}
