package com.vsdeni.ejru.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.vsdeni.ejru.R;
import com.vsdeni.ejru.adapters.DrawerAdapter;
import com.vsdeni.ejru.data.CategoriesModelColumns;
import com.vsdeni.ejru.data.HeadersModelColumns;
import com.vsdeni.ejru.fragments.HeadersFragment;
import com.vsdeni.ejru.model.Category;
import com.vsdeni.ejru.model.Header;
import com.vsdeni.ejru.network.CategoriesRequest;
import com.vsdeni.ejru.network.HeadersRequest;

import java.util.ArrayList;


public class MainActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private CategoriesRequest mCategoriesRequest;
    private HeadersRequest mHeadersRequest;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerAdapter mAdapter;
    private int mCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mAdapter = new DrawerAdapter(this, null, true);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(this);
        mCategoriesRequest = new CategoriesRequest();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_launcher, R.string.title, R.drawable.ic_launcher);
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSpiceManager().execute(mCategoriesRequest, new CategoriesRequestListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(), CategoriesModelColumns.URI, null, null, null, CategoriesModelColumns.ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) mAdapter.getItem(position);
        mCategoryId = cursor.getInt(cursor.getColumnIndex(CategoriesModelColumns.ID));
        mHeadersRequest = new HeadersRequest(mCategoryId);
        getSpiceManager().execute(mHeadersRequest, new HeadersRequestListener());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, HeadersFragment.newInstance(mCategoryId))
                .commit();
    }

    class CategoriesRequestListener implements RequestListener<Category.List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.e(TAG, spiceException.getMessage());
        }

        @Override
        public void onRequestSuccess(Category.List categories) {
            Log.i(TAG, "Categories request success");
            if (categories != null) {
                new SaveCategoriesAsyncTask().execute(categories.getCategories());
            }
        }
    }

    class HeadersRequestListener implements RequestListener<Header.List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.e(TAG, spiceException.getMessage());
        }

        @Override
        public void onRequestSuccess(Header.List headers) {
            Log.i(TAG, "Headers request success");
            if (headers != null) {
                new SaveHeadersAsyncTask().execute(mCategoryId, headers.getHeaders());
            }
        }
    }

    private class SaveCategoriesAsyncTask extends AsyncTask<ArrayList<Category>, Void, Void> {

        @Override
        protected Void doInBackground(ArrayList<Category>... params) {
            ArrayList<Category> data = params[0];
            if (data != null) {
                ContentResolver resolver = getContentResolver();
                resolver.delete(CategoriesModelColumns.URI, null, null);
                ContentValues values = new ContentValues(2);
                for (Category category : data) {
                    values.clear();
                    values.put(CategoriesModelColumns.ID, category.getId());
                    values.put(CategoriesModelColumns.NAME, category.getName());
                    resolver.insert(CategoriesModelColumns.URI, values);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getContentResolver().notifyChange(CategoriesModelColumns.URI, null);
        }
    }

    private class SaveHeadersAsyncTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            Integer categoryId = (Integer) params[0];
            ArrayList<Header> data = (ArrayList<Header>) params[1];
            if (data != null) {
                ContentResolver resolver = getContentResolver();
                resolver.delete(HeadersModelColumns.URI, null, null);
                ContentValues values = new ContentValues(4);
                for (Header header : data) {
                    values.clear();
                    values.put(HeadersModelColumns.ID, header.getId());
                    values.put(HeadersModelColumns.NAME, header.getName());
                    values.put(HeadersModelColumns.AUTHOR_ID, header.getAuthorId());
                    values.put(HeadersModelColumns.TIMESTAMP, header.getTimestamp());
                    values.put(HeadersModelColumns.CATEGORY_ID, categoryId);
                    resolver.insert(HeadersModelColumns.URI, values);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getContentResolver().notifyChange(HeadersModelColumns.URI, null);
        }
    }
}