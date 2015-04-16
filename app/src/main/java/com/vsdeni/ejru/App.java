package com.vsdeni.ejru;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vk.sdk.VKSdk;

/**
 * Created by Admin on 07.09.2014.
 */
public class App extends Application {
    private static App sInstance;

    public static App getContext() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

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

        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
