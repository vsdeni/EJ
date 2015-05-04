package com.ejnew.news.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.ejnew.news.Consts;
import com.ejnew.news.R;
import com.ejnew.news.helpers.Utils;
import com.ejnew.news.activities.BaseActivity;
import com.ejnew.news.data.ArticlesModelColumns;
import com.ejnew.news.model.Article;
import com.ejnew.news.network.ArticleRequest;

import java.util.ArrayList;

/**
 * Created by Admin on 05.09.2014.
 */
public class ArticleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = ArticleFragment.class.getSimpleName();

    private ScrollView mScrollView;
    private TextView mProgressTitle;

    private SwipeRefreshLayout mRootView;
    private WebView mWebView;

    private int mId;
    private String mTitle;
    private int mAuthorId;
    private int mCategoryId;
    private String mAuthorName;
    private View mFooter;

    private ArticleRequest mArticleRequest;

    private float mScrolled;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_article, container, false);
        mWebView = (WebView) mRootView.findViewById(R.id.body);

        mScrollView = (ScrollView) mRootView.findViewById(R.id.scroll_view);
        mProgressTitle = (TextView) mRootView.findViewById(R.id.progress_title);

        mProgressTitle.setVisibility(View.GONE);

        mRootView.setOnRefreshListener(this);
        mRootView.setColorScheme(R.color.brandBeige, R.color.brandBurgundy, R.color.brandDarkBeige, R.color.brandAlmostWhite);
        mRootView.setEnabled(false);

        mFooter = mRootView.findViewById(R.id.footer);

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

    private void moveArticleToTop(final float curMargin, final float diff) {
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

        Integer colorFrom = getResources().getColor(R.color.veryLightGray);
        Integer colorTo = getResources().getColor(android.R.color.black);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mProgressTitle.setTextColor((Integer) animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(400);
        colorAnimation.start();
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (isAdded()) {
            if (data != null && data.moveToFirst()) {
                mRootView.setRefreshing(false);
                final int topMargin = 0;
                final int curMargin = ((RelativeLayout.LayoutParams) (mScrollView.getLayoutParams())).topMargin;
                final int diff = curMargin - topMargin;
                mProgressTitle.setVisibility(View.VISIBLE);
                if (diff > 0) {
                    moveArticleToTop(curMargin, diff);
                } else {
                    mProgressTitle.setTextColor(getResources().getColor(android.R.color.black));
                }
                Article article = Article.toArticle(data);

                WebSettings webSettings = mWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                } else {
                    webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                }
                mWebView.setWebViewClient(new EjWebClient(new PageListener() {
                    @Override
                    public void onFinished() {
                        if (isAdded() && mFooter != null) {
                            mFooter.setVisibility(View.VISIBLE);
                        }
                        if (isAdded()) {
                            restoreListViewPosition();
                        }
                    }
                }));

                mWebView.loadDataWithBaseURL(Consts.BASE_URL, getHtmlData(article.getBody()), "text/html", "utf-8", null);
            } else {
                mRootView.post(new Runnable() {
                    @Override
                    public void run() {
                        mRootView.setRefreshing(true);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mScrollView.getLayoutParams();
                        params.topMargin = ((Utils.getScreenHeight(getActivity()) - Utils.getActionbarHeight(getActivity())) / 2) - (int) Utils.convertDpToPixel(100, getActivity());
                        mScrollView.setLayoutParams(params);
                        mProgressTitle.setVisibility(View.VISIBLE);
                    }
                });
                ((BaseActivity) getActivity()).getSpiceManager().execute(mArticleRequest, new ArticleRequestListener());
            }
        }
    }

    private void restoreListViewPosition() {
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = mScrollView.getHeight();
                if (height > 0) {
                    int scrollTo = (int) (((float) mScrollView.getHeight() / 100f) * mScrolled);
                    mScrollView.scrollTo(0, scrollTo);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onRefresh() {
        mRootView.setRefreshing(false);
    }

    class ArticleRequestListener implements RequestListener<Article.List> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.e(TAG, spiceException.getMessage());
            if (isAdded()) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.error_missed_connection), Toast.LENGTH_SHORT).show();
            }
            mRootView.setRefreshing(false);
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
                    values.put(ArticlesModelColumns.BODY, article.getBody());
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int scrolled = mScrollView.getScrollY();
        if (scrolled > 0) {
            outState.putFloat("scroll_position", (float) scrolled / ((float) mScrollView.getHeight() / 100f));
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mScrolled = savedInstanceState.getFloat("scroll_position");
            restoreListViewPosition();
        }
    }

    private interface PageListener {
        void onFinished();
    }

    private class EjWebClient extends WebViewClient {
        private PageListener mPageListener;

        public EjWebClient(PageListener listener) {
            mPageListener = listener;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mPageListener.onFinished();
        }
    }
}