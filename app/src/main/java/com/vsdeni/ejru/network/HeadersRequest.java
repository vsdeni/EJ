package com.vsdeni.ejru.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.vsdeni.ejru.model.Header;

/**
 * Created by Admin on 03.09.2014.
 */
public class HeadersRequest extends RetrofitSpiceRequest<Header.List, EjApi> {
    public HeadersRequest() {
        super(Header.List.class, EjApi.class);
    }

    @Override
    public Header.List loadDataFromNetwork() throws Exception {
        return getService().getAllHeaders();
    }
}
