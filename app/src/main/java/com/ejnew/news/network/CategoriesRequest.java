package com.ejnew.news.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.ejnew.news.model.Category;

/**
 * Created by Admin on 03.09.2014.
 */
public class CategoriesRequest extends RetrofitSpiceRequest<Category.List, EjApi> {

    public CategoriesRequest() {
        super(Category.List.class, EjApi.class);
    }

    @Override
    public Category.List loadDataFromNetwork() throws Exception {
        return getService().getCategories();
    }
}
