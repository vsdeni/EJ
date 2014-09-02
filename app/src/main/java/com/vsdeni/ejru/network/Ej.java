package com.vsdeni.ejru.network;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;
import com.vsdeni.ejru.model.Category;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Admin on 11.08.2014.
 */
public interface Ej {
    @GET("/?a=get_categories")
    Category.List getCategories();
}