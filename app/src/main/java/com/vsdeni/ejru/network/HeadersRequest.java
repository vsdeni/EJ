package com.vsdeni.ejru.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.vsdeni.ejru.model.Header;

/**
 * Created by Admin on 03.09.2014.
 */
public class HeadersRequest extends RetrofitSpiceRequest<Header.List, EjApi> {
    private int mCategoryId;

    public HeadersRequest(int categoryId) {
        super(Header.List.class, EjApi.class);
        mCategoryId = categoryId;
    }

    @Override
    public Header.List loadDataFromNetwork() throws Exception {
        return getService().getHeaders(mCategoryId);
    }
}
