package com.vsdeni.ejru;

import android.app.Application;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Admin on 07.09.2014.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

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
}
