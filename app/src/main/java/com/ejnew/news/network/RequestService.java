package com.ejnew.news.network;

import android.app.Notification;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;
import com.ejnew.news.Consts;

/**
 * Created by Admin on 11.08.2014.
 */
public class RequestService extends RetrofitGsonSpiceService {

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(EjApi.class);
    }

    @Override
    public Notification createDefaultNotification() {
        //Returning null we won't start the service in foreground.
        return null;
    }

    @Override
    protected String getServerUrl() {
        return Consts.BASE_API_URL;
    }
}
