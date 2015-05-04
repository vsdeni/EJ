package com.ejnew.news.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.ejnew.news.model.Header;

/**
 * Created by Admin on 03.09.2014.
 */
public class CategoryHeadersRequest extends RetrofitSpiceRequest<Header.List, EjApi> {
    private int mCategoryId;

    public CategoryHeadersRequest(int categoryId) {
        super(Header.List.class, EjApi.class);
        mCategoryId = categoryId;
    }

    @Override
    public Header.List loadDataFromNetwork() throws Exception {
        return getService().getHeaders(mCategoryId);
    }
}
