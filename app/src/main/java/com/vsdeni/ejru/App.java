package com.vsdeni.ejru;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vsdeni.ejru.helpers.ShareHelper;
import com.vsdeni.ejru.listeners.VKRequestListener;

import io.fabric.sdk.android.Fabric;

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
