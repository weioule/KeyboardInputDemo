package com.example.weioule.inputkeyboarddemo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Author by weioule.
 * Date on 2018/11/19.
 */
public class LoaderImpl {
    // 内存中的软应用缓存
    private Map<String, SoftReference<Bitmap>> imageCache;
    // 是否缓存图片至本地文件
    private boolean cache2FileFlag = false;
    private String cachedDir;

    public LoaderImpl(Map<String, SoftReference<Bitmap>> imageCache) {
        this.imageCache = imageCache;
    }

    /**
     * 是否缓存图片至外部文件
     *
     * @param flag
     */
    public void setCache2File(boolean flag) {
        cache2FileFlag = flag;
    }

    /**
     * 设置缓存图片到外部文件的路径
     *
     * @param cacheDir
     */
    public void setCachedDir(String cacheDir) {
        this.cachedDir = cacheDir;
    }

    /**
     * 从网络端下载图片
     *
     * @param url          网络图片的URL地址
     * @param cache2Memory 是否缓存(缓存在内存中)
     * @return bitmap 图片bitmap结构
     */
    public Bitmap getBitmapFromUrl(String url, boolean cache2Memory) {
        Bitmap bitmap = null;
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                if (bitmap == null) {
                    return bitmap;
                }

                if (cache2Memory) {//缓存bitmap至内存软引用中
                    imageCache.put(url, new SoftReference<Bitmap>(bitmap));
                }
                if (cache2FileFlag) {//缓存bitmap至文件系统
                    String fileName = getMD5Str(url);
                    String filePath = this.cachedDir + fileName;
                    File file = new File(filePath);
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(filePath);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                }
                is.close();
                conn.disconnect();
                return bitmap;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从内存缓存中获取bitmap
     *
     * @param url
     * @return bitmap or null.
     */
    public Bitmap getBitmapFromMemory(String url) {
        Bitmap bitmap = null;
        if (imageCache.containsKey(url)) {
            synchronized (imageCache) {
                SoftReference<Bitmap> bitmapRef = imageCache.get(url);
                if (bitmapRef != null) {
                    bitmap = bitmapRef.get();
                    return bitmap;
                }
            }
        }
        // 从外部缓存文件读取
        if (cache2FileFlag) {
            bitmap = getBitmapFromFile(url);
            if (bitmap != null) {
                imageCache.put(url, new SoftReference<>(bitmap));
            }
        }

        return bitmap;
    }

    /**
     * 从外部文件缓存中获取bitmap
     *
     * @param url
     * @return
     */
    private Bitmap getBitmapFromFile(String url) {
        Bitmap bitmap = null;
        String fileName = getMD5Str(url);
        if (fileName == null) {
            return null;
        }

        String filePath = cachedDir + fileName;

        try {
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(fis);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * MD5 加密
     */
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }

        return md5StrBuff.toString();
    }
}
