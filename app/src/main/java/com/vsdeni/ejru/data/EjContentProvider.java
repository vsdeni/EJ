package com.vsdeni.ejru.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Admin on 04.09.2014.
 */
public class EjContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.vsdeni.ejru.data.EjContentProvider";

    public static final String DB_NAME = "ej.db";
    public static final int DB_VERSION = 1;

    public static final String CATEGORIES_TABLE_NAME = "categories";

    private static final int CATEGORIES = 1;
    private static final int CATEGORY = 2;

    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static EjDatabaseHelper sDbHelper;


    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, CATEGORIES_TABLE_NAME, CATEGORIES);
        matcher.addURI(AUTHORITY, CATEGORIES_TABLE_NAME + "/#", CATEGORY);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        sDbHelper = new EjDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int matcherResult = sUriMatcher.match(uri);
        SQLiteQueryBuilder qb = null;
        Cursor cursor = null;
        switch (matcherResult) {
            case CATEGORIES:
            case CATEGORY:
                qb = new SQLiteQueryBuilder();
                qb.setTables(CATEGORIES_TABLE_NAME);
                break;
        }
        cursor = qb.query(sDbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int matcherResult = sUriMatcher.match(uri);
        long rowId = -1;
        String tableToInsertInto = null;
        Uri contentURIToUse = null;
        SQLiteDatabase db = sDbHelper.getWritableDatabase();

        switch (matcherResult) {
            case CATEGORIES:
                tableToInsertInto = CATEGORIES_TABLE_NAME;
                contentURIToUse = CategoriesModelColumns.URI;
                break;
            case CATEGORY:
                tableToInsertInto = CATEGORIES_TABLE_NAME;
                contentURIToUse = CategoriesModelColumns.URI;
                break;
        }

        try {
            rowId = db.insertOrThrow(tableToInsertInto, null, values);
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return null;
        }
        return ContentUris.withAppendedId(contentURIToUse, rowId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int matcherResult = sUriMatcher.match(uri);
        SQLiteDatabase db = sDbHelper.getWritableDatabase();
        switch (matcherResult) {
            case CATEGORIES:
                return db.delete(CATEGORIES_TABLE_NAME, selection, selectionArgs);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;
        int matcherResult = sUriMatcher.match(uri);
        String tableToInsertInto = null;
        SQLiteDatabase db = sDbHelper.getWritableDatabase();
        switch (matcherResult) {
            case CATEGORIES:
            case CATEGORY:
                tableToInsertInto = CATEGORIES_TABLE_NAME;
                break;
        }
        rowsUpdated = db.update(tableToInsertInto, values, selection, selectionArgs);
        return rowsUpdated;
    }

    public class EjDatabaseHelper extends SQLiteOpenHelper {
        private final String TAG = EjDatabaseHelper.class.getSimpleName();

        public EjDatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                String createStreamsTable = "CREATE TABLE " + CATEGORIES_TABLE_NAME + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + CategoriesModelColumns.ID + " INTEGER, "
                        + CategoriesModelColumns.NAME + " TEXT "
                        + ");";
                db.execSQL(createStreamsTable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        }
    }
}