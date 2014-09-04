package com.vsdeni.ejru.network;

import com.vsdeni.ejru.model.Category;
import com.vsdeni.ejru.model.Header;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Admin on 11.08.2014.
 */
public interface EjApi {
    @GET("/?a=get_categories")
    Category.List getCategories();

    @GET("/?a=get_articles")
    Header.List getHeaders(@Query("cat_id") int catId);
}