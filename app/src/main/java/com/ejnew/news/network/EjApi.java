package com.ejnew.news.network;

import com.ejnew.news.model.Article;
import com.ejnew.news.model.Author;
import com.ejnew.news.model.Category;
import com.ejnew.news.model.Header;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Admin on 11.08.2014.
 */
public interface EjApi {
    @GET("/?a=get_categories")
    Category.List getCategories();

    @GET("/?a=get_authors")
    Author.List getAuthors();

    @GET("/?a=get_articles")
    Header.List getHeaders(@Query("cat_id") int catId);

    @GET("/?a=get_all_articles")
    Header.List getAllHeaders();

    @GET("/?a=get_article")
    Article.List getArticle(@Query("art_id") int artId);
}