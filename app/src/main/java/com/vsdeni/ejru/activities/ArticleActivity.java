package com.vsdeni.ejru.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.vsdeni.ejru.R;
import com.vsdeni.ejru.fragments.ArticleFragment;

/**
 * Created by Admin on 06.09.2014.
 */
public class ArticleActivity extends BaseActivity {
    private final static String TAG = ArticleActivity.class.getSimpleName();

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article);

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

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView authorView = (TextView) mToolbar.findViewById(R.id.article_author);
        authorView.setText(authorName);
        TextView titleView = (TextView) mToolbar.findViewById(R.id.article_title);
        titleView.setText(articleTitle);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, ArticleFragment.newInstance(articleId, articleTitle, authorId, categoryId, authorName))
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