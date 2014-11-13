package com.vsdeni.ejru.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;

import com.vsdeni.ejru.R;
import com.vsdeni.ejru.fragments.ArticleFragment;

/**
 * Created by Admin on 06.09.2014.
 */
public class ArticleActivity extends BaseActivity {
    private final static String TAG = ArticleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int articleId;
        String articleTitle;
        int authorId;
        int categoryId;
        String authorName;

        Bundle args = getIntent().getExtras();
        if (args != null) {
            articleId = args.getInt("article_id");
            articleTitle = args.getString("article_title");
            authorId = args.getInt("author_id");
            categoryId = args.getInt("category_id");
            authorName = args.getString("author_name");
        } else {
            throw new IllegalArgumentException("Article id required!");
        }

        ActionBar actionBar = getActionBar();

        if (actionBar != null){
            actionBar.setTitle("");
            actionBar.setIcon(R.drawable.ic_home);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, ArticleFragment.newInstance(articleId, articleTitle, authorId, categoryId, authorName))
                    .commit();
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

}