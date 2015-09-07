package com.swivl.lyudmila.swivlloader.pager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.swivl.lyudmila.swivlloader.R;
import com.swivl.lyudmila.swivlloader.loader.AvatarLoader;

/**
 * Created by Lyudmila on 07.09.2015.
 */
public class ImagePager extends Activity {
    public static final String URL_AVATAR = "urlAvatar";
    private AvatarLoader mLoader;
    private String urlAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_pager);

        if (savedInstanceState == null)
            urlAvatar = getIntent().getStringExtra(URL_AVATAR);
        else
            urlAvatar = savedInstanceState.getString(URL_AVATAR);

        mLoader = new AvatarLoader(this);
        mLoader.DisplayImage(urlAvatar, (ImageView) findViewById(R.id.pager));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(URL_AVATAR, urlAvatar);
        super.onSaveInstanceState(outState);
    }
}
