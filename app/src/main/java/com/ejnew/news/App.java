package com.ejnew.news;

import android.app.Application;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ejnew.news.helpers.ShareHelper;

/**
 * Created by Admin on 07.09.2014.
 */
public class App extends Application {
    private static App sInstance;

    public static App getContext() {
        return sInstance;
    }

    private void initImageLoader() {
        DisplayImageOptions defaultDisplayOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();


        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new LruMemoryCache(10 * 1024 * 1024))
                .diskCacheFileCount(200)
                .diskCacheSize(30 * 1024 * 1024)
                .defaultDisplayImageOptions(defaultDisplayOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        initImageLoader();
        ShareHelper.initTwitter(this);
        ShareHelper.initFacebook(this);
        ShareHelper.initVk(this);
    }
}
