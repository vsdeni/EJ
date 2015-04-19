package com.vsdeni.ejru.helpers;

import android.content.Context;
import android.widget.Toast;

import com.facebook.FacebookSdk;
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
import com.vsdeni.ejru.App;
import com.vsdeni.ejru.R;
import com.vsdeni.ejru.listeners.VKRequestListener;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Denis on 18.04.2015.
 */
public class ShareHelper {
    public static VKParameters unpublishedContent;

    public static void notifySharingSuccess() {
        Toast.makeText(App.getContext(), App.getContext().getString(R.string.success_sharing_message), Toast.LENGTH_SHORT).show();
    }

    public static void initFacebook(Context context) {
        FacebookSdk.sdkInitialize(context);
    }

    public static void initVk(Context context) {
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
                VKSdk.setAccessToken(newToken, true);
                super.onReceiveNewToken(newToken);
                checkUnpublishedPosts(newToken);
            }

        }, Utils.getKey(context.getString(R.string.vk_app_id)), VKSdk.getAccessToken());
    }

    public static void initTwitter(Context context) {
        TwitterAuthConfig authConfig =
                new TwitterAuthConfig(Utils.getKey(context.getString(R.string.twitter_consumer_key)),
                        Utils.getKey(context.getString(R.string.twitter_consumer_secret)));
        Fabric.with(context, new Twitter(authConfig));
        Fabric.with(context, new TweetComposer());
    }
}
