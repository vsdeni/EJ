package com.vsdeni.ejru;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vsdeni.ejru.helpers.ShareHelper;
import com.vsdeni.ejru.listeners.VKRequestListener;

/**
 * Created by Admin on 07.09.2014.
 */
public class App extends Application {
    private static App sInstance;
    public static String sTokenKey = "VK_ACCESS_TOKEN";

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
        VKSdk.initialize(new VKSdkListener() {
            @Override
            public void onCaptchaError(VKError vkError) {

            }

            @Override
            public void onTokenExpired(VKAccessToken vkAccessToken) {

            }

            @Override
            public void onAccessDenied(VKError vkError) {

            }

            private void checkUnpublishedPosts(VKAccessToken token) {
                VKParameters unpublishedContent = ShareHelper.unpublishedContent;
                if (unpublishedContent != null) {
                    unpublishedContent.put(VKApiConst.USER_ID, token.userId);
                    VKApi.wall().post(unpublishedContent).executeWithListener(new VKRequestListener(unpublishedContent));
                }
            }

            @Override
            public void onReceiveNewToken(VKAccessToken newToken) {
                newToken.saveTokenToSharedPreferences(App.getContext(), sTokenKey);
                super.onReceiveNewToken(newToken);
                checkUnpublishedPosts(newToken);
            }

        }, App.getContext().getString(R.string.vk_app_id), VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));
    }
}
