package com.swivl.lyudmila.swivlloader.cache;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Lyudmila on 07.09.2015.
 */
public class MemoryCache {
    private static final String TAG = MemoryCache.class.getSimpleName();
    private Map<String, Bitmap> mCacheMap = new LinkedHashMap<>(10, 1.5f, true);
    private long mSizeCache=0;
    private long mLimitCache=1000000;

    public MemoryCache(){
        setLimit(Runtime.getRuntime().maxMemory()/4);
    }

    public void setLimit(long limit){
        mLimitCache=limit;
    }

    public Bitmap getBitmapCache(String url){
        try{
            if(!mCacheMap.containsKey(url)) return null;
            return mCacheMap.get(url);
        }catch(NullPointerException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void putBitmapCache(String url, Bitmap bitmap){
        try{
            if(mCacheMap.containsKey(url))
                mSizeCache -= getSizeBitmap(mCacheMap.get(url));
            mCacheMap.put(url, bitmap);
            mSizeCache+= getSizeBitmap(bitmap);
            checkSizeCache();
        }catch(Throwable th){
            th.printStackTrace();
        }
    }

    private void checkSizeCache() {
        if(mSizeCache>mLimitCache){
            Iterator<Map.Entry<String, Bitmap>> mIterator = mCacheMap.entrySet().iterator();//least recently accessed item will be the first one iterated
            while(mIterator.hasNext()){
                Map.Entry<String, Bitmap> entry=mIterator.next();
                mSizeCache-= getSizeBitmap(entry.getValue());
                mIterator.remove();
                if(mSizeCache<=mLimitCache)
                    break;
            }
        }
    }

    public void clearCache() {
        try{
            mCacheMap.clear();
            mSizeCache=0;
        } catch(NullPointerException ex){
            ex.printStackTrace();
        }
    }

    private long getSizeBitmap(Bitmap bitmap) {
        if(bitmap==null) return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
