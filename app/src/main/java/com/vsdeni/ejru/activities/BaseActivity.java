package com.vsdeni.ejru.activities;


import android.support.v4.app.FragmentActivity;

import com.octo.android.robospice.SpiceManager;
import com.vsdeni.ejru.network.RequestService;

/**
 * Created by Admin on 11.08.2014.
 */
public class BaseActivity extends FragmentActivity {
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

    public SpiceManager getSpiceManager() {
        return mSpiceManager;
    }
}
