package com.example.myapplication.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapImageCache {

    //Memory Cache to save the Bitmaps downloaded
    private static LruCache<String, Bitmap> mMemoryCache;

    static {
        //Static constructor invoked only on the first time when loaded into VM

        //Retrieving the current Max Memory available for the VM
        final int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;

        //Setting the cache size to be 1/8th of the Max Memory
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                //Size of the cache now returned will be the size of the entries
                //measured in kilobytes rather than the number of entries
                return bitmap.getByteCount() / 1024;
            }
        };
    }
    public static Bitmap getBitmapFromCache(String imageURLStr) {
        return mMemoryCache.get(imageURLStr);
    }
    public static void addBitmapToCache(String imageURLStr, Bitmap bitmap) {
        if (getBitmapFromCache(imageURLStr) == null
                && bitmap != null) {
            mMemoryCache.put(imageURLStr, bitmap);
        }
    }
    public static void clearCache() {
        mMemoryCache.evictAll();
    }

}
