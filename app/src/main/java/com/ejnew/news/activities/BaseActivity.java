package com.ejnew.news.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.octo.android.robospice.SpiceManager;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.ejnew.news.listeners.ShareListener;
import com.ejnew.news.listeners.VKRequestListener;
import com.ejnew.news.network.RequestService;

/**
 * Created by Admin on 11.08.2014.
 */
public abstract class BaseActivity extends ActionBarActivity implements ShareListener {
    private SpiceManager mSpiceManager = new SpiceManager(RequestService.class);
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onStart() {
        mSpiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mSpiceManager.shouldStop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
    }

    @Override
    public void onFacebookShare(ShareLinkContent content) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(content);
        }
    }

    @Override
    public void onVkShare(final VKParameters content) {
        VKRequest request = VKApi.wall().post(content);
        request.executeWithListener(new VKRequestListener(content));
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    public SpiceManager getSpiceManager() {
        return mSpiceManager;
    }
}
