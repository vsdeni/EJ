package com.ejnew.news.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.ejnew.news.R;
import com.ejnew.news.adapters.DrawerAdapter;
import com.ejnew.news.data.AuthorsModelColumns;
import com.ejnew.news.data.CategoriesModelColumns;
import com.ejnew.news.fragments.HeadersFragment;
import com.ejnew.news.model.Author;
import com.ejnew.news.model.Category;
import com.ejnew.news.network.AuthorsRequest;
import com.ejnew.news.network.CategoriesRequest;

import java.util.ArrayList;


public class MainActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private CategoriesRequest mCategoriesRequest;
    private AuthorsRequest mAuthorsRequest;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerAdapter mDrawerAdapter;

    private int mCategoryId;
    private String mCategoryName;

    Toolbar mToolbar;

    private HeadersFragment mHeadersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerList = (ListView) findViewById(R.id.left_menu);

        findViewById(R.id.about).setOnClickListener(this);

        mDrawerAdapter = new DrawerAdapter(this, null, true);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(this);
        mCategoriesRequest = new CategoriesRequest();
        mAuthorsRequest = new AuthorsRequest();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.app_name,
                R.string.app_name);
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            mHeadersFragment = new HeadersFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, mHeadersFragment, "headers")
                    .commit();
        } else {
            mHeadersFragment = (HeadersFragment) getSupportFragmentManager().findFragmentByTag("headers");
        }

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void refresh() {
        mHeadersFragment.onRefresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            refresh();
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
        mDrawerAdapter.swapCursor(data);
        if (data == null || data.getCount() == 0) {
            mHeadersFragment.mSwipeRefreshLayout.setRefreshing(true);
            getSpiceManager().execute(mCategoriesRequest, new CategoriesRequestListener());
        } else {
            setPage(0);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setPage(int position) {
        Cursor cursor = (Cursor) mDrawerAdapter.getItem(position);
        mCategoryId = cursor.getInt(cursor.getColumnIndex(CategoriesModelColumns.ID));
        mCategoryName = cursor.getString(cursor.getColumnIndex(CategoriesModelColumns.NAME));
        mHeadersFragment.setCategoryId(mCategoryId);
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setPage(position);
    }

    private void showAbout() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about:
                showAbout();
                mDrawerLayout.closeDrawers();
                break;
        }
    }

    class CategoriesRequestListener implements RequestListener<Category.List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            mHeadersFragment.mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(MainActivity.this, getString(R.string.error_missed_connection), Toast.LENGTH_SHORT).show();
            Log.e(TAG, spiceException.getMessage());
        }

        @Override
        public void onRequestSuccess(Category.List categories) {
            getSpiceManager().execute(mAuthorsRequest, new AuthorsRequestListener());
            Log.i(TAG, "Categories request success");
            if (categories != null) {
                new SaveCategoriesAsyncTask().execute(categories.getCategories());
            }
        }
    }

    class AuthorsRequestListener implements RequestListener<Author.List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            mHeadersFragment.mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(MainActivity.this, getString(R.string.error_missed_connection), Toast.LENGTH_SHORT).show();
            Log.e(TAG, spiceException.getMessage());
        }

        @Override
        public void onRequestSuccess(Author.List authors) {
            Log.i(TAG, "Authors request success");
            if (authors != null) {
                new SaveAuthorsAsyncTask().execute(authors.getAuthors());
            }
        }
    }


    private class SaveAuthorsAsyncTask extends AsyncTask<ArrayList<Author>, Void, Void> {

        @Override
        protected Void doInBackground(ArrayList<Author>... params) {
            ArrayList<Author> data = params[0];
            if (data != null) {
                ContentResolver resolver = getContentResolver();
                resolver.delete(AuthorsModelColumns.URI, null, null);
                ContentValues values = new ContentValues(2);
                for (Author author : data) {
                    values.clear();
                    values.put(AuthorsModelColumns.ID, author.getId());
                    values.put(AuthorsModelColumns.NAME, author.getName());
                    resolver.insert(AuthorsModelColumns.URI, values);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getContentResolver().notifyChange(AuthorsModelColumns.URI, null);
        }
    }

    private class SaveCategoriesAsyncTask extends AsyncTask<ArrayList<Category>, Void, Void> {

        @Override
        protected Void doInBackground(ArrayList<Category>... params) {
            ArrayList<Category> data = params[0];
            if (data != null) {
                data.add(0, new Category(0, getString(R.string.main_category)));
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}