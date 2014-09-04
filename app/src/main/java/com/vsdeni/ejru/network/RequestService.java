package com.vsdeni.ejru.network;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

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
    protected String getServerUrl() {
        return "http://ejrus.ru/api/";
    }
}
