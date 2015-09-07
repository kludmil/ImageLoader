package com.swivl.lyudmila.swivlloader.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.swivl.lyudmila.swivlloader.utils.Utils;
import com.swivl.lyudmila.swivlloader.cache.FileCache;
import com.swivl.lyudmila.swivlloader.cache.MemoryCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Lyudmila on 07.09.2015.
 */
public class AvatarLoader {
    private MemoryCache memoryCache=new MemoryCache();
    private FileCache fileCache;
    private Map<ImageView, String> imageViews= Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    private ExecutorService executorService;
    private Handler handler=new Handler();

    public AvatarLoader(Context context){
        fileCache=new FileCache(context);
        executorService= Executors.newFixedThreadPool(5);
    }

    public void DisplayImage(String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmapCache = memoryCache.getBitmapCache(url);
        if(bitmapCache != null)
            imageView.setImageBitmap(bitmapCache);
        else
        {
            queueImages(url, imageView);
            imageView.setImageResource(Utils.defaultDrawable);
        }
    }

    private void queueImages(String url, ImageView imageView)
    {
        ImageForLoading loading = new ImageForLoading(url, imageView);
        executorService.submit(new ImageLoaders(loading));
    }

    private Bitmap getBitmap(String url)
    {
        File f = fileCache.getFile(url);

        Bitmap decodeBitmap = decodeFile(f);
        if(decodeBitmap!=null)
            return decodeBitmap;

        try {
            Bitmap bitmap;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(Utils.TIMEOUT_CONNECT);
            conn.setReadTimeout(Utils.TIMEOUT_READ);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex){
            ex.printStackTrace();
            if(ex instanceof OutOfMemoryError)
                memoryCache.clearCache();
            return null;
        }
    }

    private Bitmap decodeFile(File f){
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();

            final int REQUIRED_SIZE = 150;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            FileInputStream stream2=new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class ImageForLoading
    {
        public String urlImg;
        public ImageView imageView;
        public ImageForLoading(String url, ImageView imageView){
            urlImg = url;
            this.imageView = imageView;
        }
    }

    class ImageLoaders implements Runnable {
        ImageForLoading mImageForLoading;
        ImageLoaders(ImageForLoading loading){
            this.mImageForLoading = loading;
        }

        @Override
        public void run() {
            try{
                if(imageViewReused(mImageForLoading))
                    return;
                Bitmap bmp=getBitmap(mImageForLoading.urlImg);
                memoryCache.putBitmapCache(mImageForLoading.urlImg, bmp);
                if(imageViewReused(mImageForLoading))
                    return;
                BitmapOnDisplay mBitmapDisplay = new BitmapOnDisplay(bmp, mImageForLoading);
                handler.post(mBitmapDisplay);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }

    private boolean imageViewReused(ImageForLoading loading){
        String tag = imageViews.get(loading.imageView);
        if(tag == null || !tag.equals(loading.urlImg))
            return true;
        return false;
    }

    class BitmapOnDisplay implements Runnable
    {
        Bitmap mBitmap;
        ImageForLoading mImageForLoading;
        public BitmapOnDisplay(Bitmap bitmap, ImageForLoading loading) {
            this.mBitmap = bitmap;
            mImageForLoading = loading;}
        public void run()
        {
            if(imageViewReused(mImageForLoading))
                return;
            if(mBitmap !=null)
                mImageForLoading.imageView.setImageBitmap(mBitmap);
            else
                mImageForLoading.imageView.setImageResource(Utils.defaultDrawable);
        }
    }

    public void clearCache() {
        memoryCache.clearCache();
        fileCache.clear();
    }

}
