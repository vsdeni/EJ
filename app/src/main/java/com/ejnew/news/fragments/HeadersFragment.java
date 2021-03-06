package com.ejnew.news.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.ejnew.news.R;
import com.ejnew.news.activities.ArticleActivity;
import com.ejnew.news.activities.BaseActivity;
import com.ejnew.news.adapters.HeadersAdapter;
import com.ejnew.news.data.HeadersModelColumns;
import com.ejnew.news.model.Header;
import com.ejnew.news.network.HeadersRequest;

import java.util.ArrayList;

/**
 * Created by Admin on 05.09.2014.
 */
public class HeadersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = HeadersFragment.class.getSimpleName();
    private int mCategoryId;
    private ListView mListView;
    private CursorAdapter mAdapter;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    private HeadersRequest mHeadersRequest;
    private boolean mRequestRunning;

    private boolean mIsForceUpdating;

    private Parcelable mListInstanceState;

    public static HeadersFragment newInstance(int categoryId) {
        HeadersFragment fr = new HeadersFragment();
        Bundle args = new Bundle(1);
        args.putInt("cat_id", categoryId);
        fr.setArguments(args);
        return fr;
    }

    public void setCategoryId(int categoryId) {
        mCategoryId = categoryId;
        getLoaderManager().restartLoader(1, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mCategoryId = args.getInt("cat_id");
        }

        if (savedInstanceState != null) {
            mListInstanceState = savedInstanceState.getParcelable("list");
        }

        mHeadersRequest = new HeadersRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_headers, container, false);
        mListView = (ListView) view.findViewById(R.id.lv_headers);
        mListView.setOnItemClickListener(this);
        mAdapter = new HeadersAdapter(getActivity(), null, true);
        mListView.setAdapter(mAdapter);

        if (mListView != null && mListInstanceState != null) {
            mListView.onRestoreInstanceState(mListInstanceState);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mCategoryId == 0) {
            return new CursorLoader(getActivity(), HeadersModelColumns.URI, null, null, null, HeadersModelColumns.TIMESTAMP + " DESC");
        } else {
            return new CursorLoader(getActivity(), HeadersModelColumns.URI, null, HeadersModelColumns.CATEGORY_ID + " = " + mCategoryId, null, HeadersModelColumns.TIMESTAMP + " DESC");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) {
            mSwipeRefreshLayout.setRefreshing(true);
            onRefresh();
        }
        mAdapter.swapCursor(data);
        if (!mIsForceUpdating) {
            if (mListView != null && mListInstanceState != null) {
                mListView.onRestoreInstanceState(mListInstanceState);
            }
        } else {
            mIsForceUpdating = false;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) mAdapter.getItem(position);
        Header header = Header.toHeader(cursor);
        Intent intent = new Intent(getActivity(), ArticleActivity.class);
        intent.putExtra("article_id", header.getId());
        intent.putExtra("category_id", header.getCategoryId());
        intent.putExtra("author_id", header.getAuthorId());
        intent.putExtra("article_title", header.getName());
        intent.putExtra("article_spoiler", header.getSpoiler());
        String authorName = cursor.getString(cursor.getColumnIndex("author_name"));
        intent.putExtra("author_name", authorName == null ? "" : authorName);
        intent.putExtra("has_thumbnail", view.findViewById(R.id.thumbnail).getVisibility() == View.VISIBLE);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mIsForceUpdating = true;
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }

        if (!mRequestRunning) {
            mRequestRunning = true;
            ((BaseActivity) getActivity()).getSpiceManager().execute(mHeadersRequest, new HeadersRequestListener());
        }
    }

    class HeadersRequestListener implements RequestListener<Header.List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.e(TAG, spiceException.getMessage());
            if (isAdded()) {
                Toast.makeText(getActivity(), getString(R.string.error_missed_connection), Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                mRequestRunning = false;
            }
        }

        @Override
        public void onRequestSuccess(Header.List headers) {
            Log.i(TAG, "Headers request success");
            mRequestRunning = false;
            if (headers != null) {
                new SaveHeadersAsyncTask().execute(mCategoryId, headers.getHeaders());
            }
        }
    }

    private class SaveHeadersAsyncTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            ArrayList<Header> data = (ArrayList<Header>) params[1];
            if (data != null) {
                ContentResolver resolver = getActivity().getContentResolver();
                resolver.delete(HeadersModelColumns.URI, null, null);
                ContentValues values = new ContentValues(4);
                for (Header header : data) {
                    values.clear();
                    values.put(HeadersModelColumns.ID, header.getId());
                    values.put(HeadersModelColumns.NAME, header.getName());
                    values.put(HeadersModelColumns.AUTHOR_ID, header.getAuthorId());
                    values.put(HeadersModelColumns.TIMESTAMP, header.getTimestamp());
                    values.put(HeadersModelColumns.CATEGORY_ID, header.getCategoryId());
                    values.put(HeadersModelColumns.SPOILER, header.getSpoiler());
                    values.put(HeadersModelColumns.THUMBNAIL_URL, header.getThumbnailUrl());
                    resolver.insert(HeadersModelColumns.URI, values);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (isAdded()) {
                getActivity().getContentResolver().notifyChange(HeadersModelColumns.URI, null);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("list", mListView.onSaveInstanceState());
    }

}