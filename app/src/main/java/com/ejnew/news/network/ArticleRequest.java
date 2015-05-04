package com.ejnew.news.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.ejnew.news.model.Article;

/**
 * Created by Admin on 03.09.2014.
 */
public class ArticleRequest extends RetrofitSpiceRequest<Article.List, EjApi> {
    private int mArtId;

    public ArticleRequest(int artId) {
        super(Article.List.class, EjApi.class);
        mArtId = artId;
    }

    @Override
    public Article.List loadDataFromNetwork() throws Exception {
        return getService().getArticle(mArtId);
    }
}
