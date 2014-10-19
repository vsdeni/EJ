package com.vsdeni.ejru.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.vsdeni.ejru.R;
import com.vsdeni.ejru.Utils;
import com.vsdeni.ejru.activities.BaseActivity;
import com.vsdeni.ejru.data.ArticlesModelColumns;
import com.vsdeni.ejru.model.Article;
import com.vsdeni.ejru.network.ArticleRequest;
import com.vsdeni.ejru.views.PinchToZoomTextView;

import java.util.ArrayList;

/**
 * Created by Admin on 05.09.2014.
 */
public class ArticleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ArticleFragment.class.getSimpleName();

    private ScrollView mScrollView;
    private TextView mProgressTitle;
    private ProgressBar mProgressbar;

    private View mRootView;
    private PinchToZoomTextView mBody;
    private ImageView mImage;

    private int mId;
    private String mTitle;
    private int mAuthorId;
    private int mCategoryId;
    private String mAuthorName;

    private float mStartingFontSize;

    private ArticleRequest mArticleRequest;

    public static ArticleFragment newInstance(int id, String title, int authorId, int categoryId, String authorName) {
        ArticleFragment fr = new ArticleFragment();
        Bundle args = new Bundle(1);
        args.putInt("id", id);
        args.putString("title", title);
        args.putInt("author_id", authorId);
        args.putInt("category", categoryId);
        args.putString("author_name", authorName);
        fr.setArguments(args);
        return fr;
    }

    @Override
    public void onPause() {
        if (mStartingFontSize != mBody.getTextSize()) {
            Utils.Prefs.saveInt(Utils.Prefs.FONT_SIZE, Utils.pixelsToSp(mBody.getTextSize(), getActivity()), getActivity());
        }
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article, container, false);
        mBody = (PinchToZoomTextView) mRootView.findViewById(R.id.tv_article_body);
        mBody.setMovementMethod(LinkMovementMethod.getInstance());

        int customTextSize = Utils.Prefs.getInt(Utils.Prefs.FONT_SIZE, 0, getActivity());

        if (customTextSize != 0) {
            mBody.setTextSize(customTextSize);
        }

        mStartingFontSize = mBody.getTextSize();

        mScrollView = (ScrollView) mRootView.findViewById(R.id.scroll_view);
        mProgressTitle = (TextView) mRootView.findViewById(R.id.progress_title);
        mProgressbar = (ProgressBar) mRootView.findViewById(R.id.progressbar);

        mProgressTitle.setText(mTitle + "\n" + mAuthorName);

        return mRootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mId = args.getInt("id");
            mTitle = args.getString("title");
            mAuthorId = args.getInt("author_id");
            mCategoryId = args.getInt("category");
            mAuthorName = args.getString("author_name");
        } else {
            throw new IllegalArgumentException("Article id required!");
        }
        getActivity().getSupportLoaderManager().initLoader(3, null, this);

        mArticleRequest = new ArticleRequest(mId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ArticlesModelColumns.URI, null, ArticlesModelColumns.ID + " = " + mId, null, null);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (isAdded()) {
            if (data != null && data.moveToFirst()) {
                setProgressVisible(false);
                final int topMargin = 0;
                final int curMargin = ((RelativeLayout.LayoutParams) (mScrollView.getLayoutParams())).topMargin;
                final int diff = curMargin - topMargin;
                mProgressTitle.setVisibility(View.VISIBLE);
                if (diff > 0) {
                    Animation a = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mScrollView.getLayoutParams();
                            params.topMargin = (int) (curMargin - (diff * interpolatedTime));
                            mScrollView.setLayoutParams(params);
                        }
                    };
                    a.setDuration(400);
                    mScrollView.startAnimation(a);
                }
                Article article = Article.toArticle(data);
                mBody.setText(Html.fromHtml(article.getBody()));
            } else {
                mRootView.post(new Runnable() {
                    @Override
                    public void run() {
                        setProgressVisible(true);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mScrollView.getLayoutParams();
                        params.topMargin = ((Utils.getScreenHeight(getActivity()) -Utils.getActionbarHeight(getActivity()))/ 2) - (int) Utils.convertDpToPixel(75, getActivity());
                        mScrollView.setLayoutParams(params);
                        mProgressTitle.setVisibility(View.VISIBLE);
                    }
                });
                ((BaseActivity) getActivity()).getSpiceManager().execute(mArticleRequest, new ArticleRequestListener());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setProgressVisible(boolean visible) {
        if (visible) {
            mProgressbar.setVisibility(View.VISIBLE);
        } else {
            mProgressbar.setVisibility(View.GONE);
        }
    }

    class ArticleRequestListener implements RequestListener<Article.List> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.e(TAG, spiceException.getMessage());
            setProgressVisible(false);
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
                ContentResolver resolver = getActivity().getContentResolver();
                resolver.delete(ArticlesModelColumns.URI, ArticlesModelColumns.ID + " = " + mId, null);
                ContentValues values = new ContentValues(5);
                for (Article article : data) {
                    values.clear();
                    values.put(ArticlesModelColumns.ID, mId);
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
            getActivity().getContentResolver().notifyChange(ArticlesModelColumns.URI, null);
        }
    }
}