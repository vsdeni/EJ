package com.vsdeni.ejru.listeners;

import android.util.Log;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vsdeni.ejru.helpers.ShareHelper;

/**
 * Created by Denis on 18.04.2015.
 */
public class VKRequestListener extends VKRequest.VKRequestListener {
    private VKParameters mContent;

    public VKRequestListener(VKParameters content) {
        mContent = content;
    }

    @Override
    public void onComplete(VKResponse response) {
        ShareHelper.notifySharingSuccess();
        ShareHelper.unpublishedContent = null;
    }

    @Override
    public void onError(VKError error) {
        Log.e("vk", error.toString());
        if (error.errorCode == -101) {
            ShareHelper.unpublishedContent = mContent;
            VKSdk.authorize(VKScope.WALL);
        }
    }
}
