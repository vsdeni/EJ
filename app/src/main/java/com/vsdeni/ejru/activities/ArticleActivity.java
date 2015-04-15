package com.vsdeni.ejru.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.vsdeni.ejru.Consts;
import com.vsdeni.ejru.R;
import com.vsdeni.ejru.dialogs.AppShareDialog;
import com.vsdeni.ejru.fragments.ArticleFragment;
import com.vsdeni.ejru.helpers.UrlGenerator;

/**
 * Created by Admin on 06.09.2014.
 */
public class ArticleActivity extends BaseActivity {
    private final static String TAG = ArticleActivity.class.getSimpleName();

    Toolbar mToolbar;
    ImageButton mShareButton;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    String mArticleTitle;
    int mArticleId;
    String mArticleSpoiler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article);

        int authorId;
        int categoryId;
        String authorName;

        Bundle args = getIntent().getExtras();
        if (args != null) {
            mArticleId = args.getInt("article_id");
            mArticleTitle = args.getString("article_title");
            mArticleSpoiler = args.getString("article_spoiler");
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
        titleView.setText(mArticleTitle);

        if (savedInstanceState == null) {
            ArticleFragment fragment = ArticleFragment.newInstance(mArticleId, mArticleTitle, authorId, categoryId, authorName);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, fragment)
                    .commit();
        }

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
    }

    private void shareFacebook() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(mArticleTitle)
                    .setImageUrl(Uri.parse(Consts.BASE_URL + "/img/content/Notes/" + mArticleId + "/anons/anons350.jpg"))
                    .setContentDescription(Html.fromHtml(mArticleSpoiler).toString())
                    .setContentUrl(Uri.parse(Consts.BASE_URL + "?a=note&id=" + mArticleId))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_share:
                showDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    void showDialog() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        String shareTitle = mArticleTitle;
        String shareBody = Html.fromHtml(mArticleSpoiler).toString();
        String shareImage = UrlGenerator.forImage(this, mArticleId);
        String shareUrl = UrlGenerator.forArticle(this, mArticleId);

        // Create and show the dialog.
        DialogFragment newFragment = AppShareDialog.newInstance(shareTitle, shareBody, shareImage, shareUrl);
        newFragment.show(ft, "dialog");
    }
}