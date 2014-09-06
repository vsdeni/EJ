package com.vsdeni.ejru.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.CursorAdapter;

import com.vsdeni.ejru.adapters.HeadersAdapter;
import com.vsdeni.ejru.data.HeadersModelColumns;

/**
 * Created by Admin on 05.09.2014.
 */
public class HeadersFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private int mCategoryId;

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

        setListAdapter(new HeadersAdapter(getActivity(), null, true));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), HeadersModelColumns.URI, null, HeadersModelColumns.CATEGORY_ID + " = " + mCategoryId, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((CursorAdapter) getListAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}