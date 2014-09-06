package com.vsdeni.ejru.model;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.vsdeni.ejru.data.ArticlesModelColumns;

import java.util.ArrayList;

/**
 * Created by Admin on 06.09.2014.
 */
public class Article {
    private int mId;

    @SerializedName("body")
    private String mBody;

    @SerializedName("image_url")
    private String mImageUrl;

    private int mAuthorId;

    private int mCategoryId;

    public static Article toArticle(Cursor cursor) {
        if (cursor != null) {
            Article article = new Article();
            article.setId(cursor.getInt(cursor.getColumnIndex(ArticlesModelColumns.ID)));
            article.setAuthorId(cursor.getInt(cursor.getColumnIndex(ArticlesModelColumns.AUTHOR_ID)));
            article.setBody(cursor.getString(cursor.getColumnIndex(ArticlesModelColumns.BODY)));
            article.setCategoryId(cursor.getInt(cursor.getColumnIndex(ArticlesModelColumns.CATEGORY_ID)));
            article.setImageUrl(cursor.getString(cursor.getColumnIndex(ArticlesModelColumns.IMAGE_URL)));
            return article;
        }
        return null;
    }

    public int getAuthorId() {
        return mAuthorId;
    }

    public void setAuthorId(int authorId) {
        mAuthorId = authorId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(int categoryId) {
        mCategoryId = categoryId;
    }

    public static class List {
        @SerializedName("articles")
        private ArrayList<Article> mArticles;

        public ArrayList<Article> getArticles() {
            return mArticles;
        }
    }
}
