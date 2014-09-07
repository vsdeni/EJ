package com.vsdeni.ejru.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vsdeni.ejru.R;
import com.vsdeni.ejru.data.ArticlesModelColumns;
import com.vsdeni.ejru.model.Article;

/**
 * Created by Admin on 05.09.2014.
 */
public class ArticleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private int mId;

    private TextView mBody;
    private ImageView mImage;

    public static ArticleFragment newInstance(int id) {
        ArticleFragment fr = new ArticleFragment();
        Bundle args = new Bundle(1);
        args.putInt("id", id);
        fr.setArguments(args);
        return fr;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        mBody = (TextView) view.findViewById(R.id.tv_article_body);
        mBody.setMovementMethod(LinkMovementMethod.getInstance());
        mImage = (ImageView) view.findViewById(R.id.iv_article_image);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mId = args.getInt("id");
        } else {
            throw new IllegalArgumentException("Article id required!");
        }
        getActivity().getSupportLoaderManager().initLoader(3, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), ArticlesModelColumns.URI, null, ArticlesModelColumns.ID + " = " + mId, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (isAdded()) {
            if (data != null && data.moveToFirst()) {
                Article article = Article.toArticle(data);
                mBody.setText(Html.fromHtml(article.getBody()));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}