package com.example.weioule.inputkeyboarddemo.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.example.weioule.inputkeyboarddemo.util.AsyncImageLoaderProxy;

/**
 * Author by weioule.
 * Date on 2018/11/19.
 */
public class AsyncImageView extends AppCompatImageView {
    AsyncImageLoaderProxy loader;

    public AsyncImageView(Context context) {
        super(context);
        loader = new AsyncImageLoaderProxy(getContext().getApplicationContext());
    }

    public AsyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loader = new AsyncImageLoaderProxy(getContext().getApplicationContext());
    }

    /**
     * 缓存文件到指定路径；当缓存超过5M时，将清空缓存
     */
    public void downloadCache2Sd(String url) {
        loader.downloadCache2Sd(url, new CallBack());
    }

    /**
     * 图片缓存到内存，只做soft缓存
     */
    public void downloadCache2memory(String url) {
        loader.downloadCache2memory(url, new CallBack());
    }

    /**
     * 缓存文件到指定路径；程序不会主动清除缓存
     */
    public void downloadCacheForever(String url) {
        loader.downloadCacheForever(url, new CallBack());
    }

    class CallBack implements AsyncImageLoaderProxy.ImageCallback {
        @Override
        public void onImageLoaded(Bitmap bitmap, String imageUrl) {
            if (mOnLoaderListener == null) {
                if (bitmap != null) {
                    setImageBitmap(bitmap);
                }
            } else {
                mOnLoaderListener.loaded(bitmap);
            }
        }
    }

    public OnLoadedListener mOnLoaderListener;

    public void setLoadedListener(OnLoadedListener back) {
        this.mOnLoaderListener = mOnLoaderListener;
    }

    public interface OnLoadedListener {
        void loaded(Bitmap bitmap);
    }
}
