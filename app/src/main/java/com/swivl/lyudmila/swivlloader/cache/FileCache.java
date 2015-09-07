package com.swivl.lyudmila.swivlloader.cache;

import android.content.Context;
import android.net.Uri;

import com.swivl.lyudmila.swivlloader.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Lyudmila on 07.09.2015.
 */
public class FileCache {

    private File mCacheDir;

    public FileCache(Context context){
        try {
            mCacheDir = Utils.getAppFilesDir(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile(String url){
        String filename = Uri.parse(url).getLastPathSegment();
        File fileAvatar = new File(mCacheDir, filename);
        return fileAvatar;

    }

    public void clear(){
        File[] files= mCacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}
