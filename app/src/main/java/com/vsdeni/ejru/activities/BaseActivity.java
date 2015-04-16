package com.vsdeni.ejru.activities;


import android.content.Intent;
import android.support.v7.app.ActionBarActivity;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.octo.android.robospice.SpiceManager;
import com.vk.sdk.VKUIHelper;
import com.vsdeni.ejru.listeners.ShareListener;
import com.vsdeni.ejru.network.RequestService;

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
    public void onFacebookShare(ShareLinkContent content) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(content);
        }
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
