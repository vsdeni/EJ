package com.vsdeni.ejru.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.vsdeni.ejru.data.ArticlesModelColumns;
import com.vsdeni.ejru.fragments.ArticleFragment;
import com.vsdeni.ejru.model.Article;
import com.vsdeni.ejru.network.ArticleRequest;

import java.util.ArrayList;

/**
 * Created by Admin on 06.09.2014.
 */
public class ArticleActivity extends BaseActivity {
    private final static String TAG = ArticleActivity.class.getSimpleName();

    private boolean mArticleAlreadyInCache;

    private ArticleRequest mArticleRequest;
    private int mArticleId;
    private int mCategoryId;
    private int mAuthorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            mArticleId = args.getInt("article_id");
            mCategoryId = args.getInt("category_id");
            mAuthorId = args.getInt("author_id");
        }

        if (mArticleId == 0) {
            throw new IllegalArgumentException("Article id required!");
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, ArticleFragment.newInstance(mArticleId))
                    .commit();
        }

        Cursor cursor = getContentResolver().query(ArticlesModelColumns.URI, new String[]{ArticlesModelColumns._ID}, ArticlesModelColumns.ID + "=" + mArticleId, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            mArticleAlreadyInCache = true;
        }

        mArticleRequest = new ArticleRequest(mArticleId);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mArticleAlreadyInCache) {
            getSpiceManager().execute(mArticleRequest, new ArticleRequestListener());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class ArticleRequestListener implements RequestListener<Article.List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.e(TAG, spiceException.getMessage());
        }

        @Override
        public void onRequestSuccess(Article.List articles) {
            Log.i(TAG, "Article request success");
            if (articles != null) {
                new SaveArticleAsyncTask().execute(articles.getArticles());
            }
        }
    }

    private class SaveArticleAsyncTask extends AsyncTask<ArrayList<Article>, Void, Void> {

        private String getBodyWithoutRedundantTags(String text) {
            int tagOpenPosition = text.indexOf("<img src=");
            while (tagOpenPosition >= 0) {
                int tagEndPosition = text.indexOf(">", tagOpenPosition);
                if (tagEndPosition > 0) {
                    if (tagOpenPosition >= 3) {
                        String tagBefore = text.substring(tagOpenPosition - 3, tagOpenPosition);
                        if (tagBefore.equals("<p>")) {
                            tagOpenPosition = tagOpenPosition - 3;
                        }
                    }
                    String tagContent = text.substring(tagOpenPosition, tagEndPosition + 1);
                    text = text.replace(tagContent, "");
                }
                tagOpenPosition = text.indexOf("<img src=");
            }

            tagOpenPosition = text.indexOf("<br");
            while (tagOpenPosition == 0) {
                text = text.substring(6);
                tagOpenPosition = text.indexOf("<br");
            }

            return text;
        }

        @Override
        protected Void doInBackground(ArrayList<Article>... params) {
            ArrayList<Article> data = params[0];
            if (data != null) {
                ContentResolver resolver = getContentResolver();
                resolver.delete(ArticlesModelColumns.URI, ArticlesModelColumns.ID + " = " + mArticleId, null);
                ContentValues values = new ContentValues(5);
                for (Article article : data) {
                    values.clear();
                    values.put(ArticlesModelColumns.ID, mArticleId);
                    values.put(ArticlesModelColumns.BODY, getBodyWithoutRedundantTags(article.getBody()));
                    values.put(ArticlesModelColumns.IMAGE_URL, article.getImageUrl());
                    values.put(ArticlesModelColumns.AUTHOR_ID, mAuthorId);
                    values.put(ArticlesModelColumns.CATEGORY_ID, mCategoryId);
                    resolver.insert(ArticlesModelColumns.URI, values);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getContentResolver().notifyChange(ArticlesModelColumns.URI, null);
        }
    }
}