package com.swivl.lyudmila.swivlloader.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.swivl.lyudmila.swivlloader.data.ListData;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Lyudmila on 07.09.2015.
 */
public class Utils {

    public static final int defaultDrawable = android.R.drawable.ic_menu_gallery;
    private static final String RESERVE_APP_FILES_FOLDER = "/mnt/sdcard/Android/data/%s/cache/";
    public static final String URL_DATA_USERS = "https://api.github.com/users";
    public static final int TIMEOUT_READ = 40000;
    public static final int TIMEOUT_CONNECT = 40000;

    private static ListData[] listData;

    public static ListData[] JSONParserMethod(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            listData = new ListData[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                String strAvatar = jsonArray.getJSONObject(i).getString("avatar_url");
                String strLogin = jsonArray.getJSONObject(i).getString("login");
                String strUrlGit = jsonArray.getJSONObject(i).getString("html_url");

                listData [i] = new ListData (strAvatar, strLogin, strUrlGit);
            }
        } catch (JSONException e) {
            Log.e("JSON", "There was an error parsing the JSON", e);
        }

        return listData;
    }

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int sizeBuffer=1024;
        try
        {
            byte[] bytes=new byte[sizeBuffer];
            for(;;)
            {
                int count=is.read(bytes, 0, sizeBuffer);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static
    @NotNull
    File getAppFilesDir(@NotNull final Context context) throws IOException {
        File appCacheFile;

        if (isExternalStorageMounted()) {
            appCacheFile = context.getExternalCacheDir();
            if (appCacheFile != null && (appCacheFile.mkdirs() || appCacheFile.isDirectory())) {
                return appCacheFile;
            }
        }

        appCacheFile = new File(String.format(RESERVE_APP_FILES_FOLDER, context.getPackageName()));
        if (appCacheFile.mkdirs() || appCacheFile.isDirectory()) {
            return appCacheFile;
        }

        appCacheFile = context.getCacheDir();
        if (appCacheFile != null && (appCacheFile.mkdirs() || appCacheFile.isDirectory())) {
            return appCacheFile;
        }

        throw new IOException();
    }

    public static boolean isExternalStorageMounted() {
        return !Environment.isExternalStorageRemovable() || Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean checkConnection(Context context) {
        boolean check = true;
        final ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            check = false;
        }
        return check;
    }

}
