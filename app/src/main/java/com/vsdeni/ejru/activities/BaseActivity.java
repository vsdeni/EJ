package com.vsdeni.ejru.activities;

import android.app.Activity;

import com.octo.android.robospice.SpiceManager;
import com.vsdeni.ejru.network.RequestService;

/**
 * Created by Admin on 11.08.2014.
 */
public class BaseActivity extends Activity{
    private SpiceManager mSpiceManager = new SpiceManager(RequestService.class);

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

    protected SpiceManager getSpiceManager() {
        return mSpiceManager;
    }
}
