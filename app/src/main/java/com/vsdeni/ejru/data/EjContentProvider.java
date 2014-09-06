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

import java.util.HashMap;

/**
 * Created by Admin on 04.09.2014.
 */
public class EjContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.vsdeni.ejru.data.EjContentProvider";

    public static final String DB_NAME = "ej.db";
    public static final int DB_VERSION = 1;

    public static final String CATEGORIES_TABLE_NAME = "categories";
    public static final String HEADERS_TABLE_NAME = "headers";
    public static final String AUTHORS_TABLE_NAME = "authors";
    public static final String ARTICLES_TABLE_NAME = "articles";

    private static final int CATEGORIES = 1;
    private static final int CATEGORY = 2;
    private static final int HEADERS = 3;
    private static final int HEADER = 4;
    private static final int AUTHORS = 5;
    private static final int AUTHOR = 6;
    private static final int ARTICLES = 7;
    private static final int ARTICLE = 8;

    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static EjDatabaseHelper sDbHelper;


    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, CATEGORIES_TABLE_NAME, CATEGORIES);
        matcher.addURI(AUTHORITY, CATEGORIES_TABLE_NAME + "/#", CATEGORY);
        matcher.addURI(AUTHORITY, HEADERS_TABLE_NAME, HEADERS);
        matcher.addURI(AUTHORITY, HEADERS_TABLE_NAME + "/#", HEADER);
        matcher.addURI(AUTHORITY, AUTHORS_TABLE_NAME, AUTHORS);
        matcher.addURI(AUTHORITY, AUTHORS_TABLE_NAME + "/#", AUTHOR);
        matcher.addURI(AUTHORITY, ARTICLES_TABLE_NAME, ARTICLES);
        matcher.addURI(AUTHORITY, ARTICLES_TABLE_NAME + "/#", ARTICLE);
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
            case HEADERS:
            case HEADER:
                qb = new SQLiteQueryBuilder();
                HashMap<String, String> columnMap = new HashMap<String, String>(2);
                columnMap.put(HEADERS_TABLE_NAME + "." + HeadersModelColumns._ID, HEADERS_TABLE_NAME + "." + HeadersModelColumns._ID);
                columnMap.put(HEADERS_TABLE_NAME + "." + HeadersModelColumns.ID, HEADERS_TABLE_NAME + "." + HeadersModelColumns.ID);
                columnMap.put(AUTHORS_TABLE_NAME + "." + AuthorsModelColumns.ID, AUTHORS_TABLE_NAME + "." + AuthorsModelColumns.ID + " AS a_id");
                columnMap.put(HEADERS_TABLE_NAME + "." + HeadersModelColumns.NAME, HEADERS_TABLE_NAME + "." + HeadersModelColumns.NAME);
                columnMap.put(AUTHORS_TABLE_NAME + "." + AuthorsModelColumns.NAME, AUTHORS_TABLE_NAME + "." + AuthorsModelColumns.NAME + " AS author_name");
                columnMap.put(HEADERS_TABLE_NAME + "." + HeadersModelColumns.CATEGORY_ID, HEADERS_TABLE_NAME + "." + HeadersModelColumns.CATEGORY_ID);
                columnMap.put(HEADERS_TABLE_NAME + "." + HeadersModelColumns.TIMESTAMP, HEADERS_TABLE_NAME + "." + HeadersModelColumns.TIMESTAMP);
                String tables = HEADERS_TABLE_NAME + " LEFT OUTER JOIN " + AUTHORS_TABLE_NAME + " ON (" + HEADERS_TABLE_NAME + "." + HeadersModelColumns.AUTHOR_ID + " = " + AUTHORS_TABLE_NAME + "." + AuthorsModelColumns.ID + ")";
                qb.setProjectionMap(columnMap);
                qb.setTables(tables);
                break;
            case AUTHORS:
            case AUTHOR:
                qb = new SQLiteQueryBuilder();
                qb.setTables(AUTHORS_TABLE_NAME);
                break;
            case ARTICLES:
            case ARTICLE:
                qb = new SQLiteQueryBuilder();
                qb.setTables(ARTICLES_TABLE_NAME);
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
            case CATEGORY:
                tableToInsertInto = CATEGORIES_TABLE_NAME;
                contentURIToUse = CategoriesModelColumns.URI;
                break;
            case HEADERS:
            case HEADER:
                tableToInsertInto = HEADERS_TABLE_NAME;
                contentURIToUse = HeadersModelColumns.URI;
                break;
            case AUTHORS:
            case AUTHOR:
                tableToInsertInto = AUTHORS_TABLE_NAME;
                contentURIToUse = AuthorsModelColumns.URI;
                break;
            case ARTICLES:
            case ARTICLE:
                tableToInsertInto = ARTICLES_TABLE_NAME;
                contentURIToUse = ArticlesModelColumns.URI;
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
            case CATEGORY:
            case CATEGORIES:
                return db.delete(CATEGORIES_TABLE_NAME, selection, selectionArgs);
            case HEADER:
            case HEADERS:
                return db.delete(HEADERS_TABLE_NAME, selection, selectionArgs);
            case AUTHOR:
            case AUTHORS:
                return db.delete(AUTHORS_TABLE_NAME, selection, selectionArgs);
            case ARTICLES:
            case ARTICLE:
                return db.delete(ARTICLES_TABLE_NAME, selection, selectionArgs);

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
            case HEADERS:
            case HEADER:
                tableToInsertInto = HEADERS_TABLE_NAME;
                break;
            case AUTHORS:
            case AUTHOR:
                tableToInsertInto = AUTHORS_TABLE_NAME;
                break;
            case ARTICLES:
            case ARTICLE:
                tableToInsertInto = ARTICLES_TABLE_NAME;
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
                String createCategoriesTable = "CREATE TABLE " + CATEGORIES_TABLE_NAME + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + CategoriesModelColumns.ID + " INTEGER, "
                        + CategoriesModelColumns.NAME + " TEXT "
                        + ");";
                db.execSQL(createCategoriesTable);

                String createHeadersTable = "CREATE TABLE " + HEADERS_TABLE_NAME + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + HeadersModelColumns.ID + " INTEGER, "
                        + HeadersModelColumns.NAME + " TEXT, "
                        + HeadersModelColumns.AUTHOR_ID + " INTEGER, "
                        + HeadersModelColumns.CATEGORY_ID + " INTEGER, "
                        + HeadersModelColumns.TIMESTAMP + " TEXT "
                        + ");";
                db.execSQL(createHeadersTable);

                String createAuthorsTable = "CREATE TABLE " + AUTHORS_TABLE_NAME + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + AuthorsModelColumns.ID + " INTEGER, "
                        + AuthorsModelColumns.NAME + " TEXT "
                        + ");";
                db.execSQL(createAuthorsTable);

                String createArticlesTable = "CREATE TABLE " + ARTICLES_TABLE_NAME + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + ArticlesModelColumns.ID + " INTEGER, "
                        + ArticlesModelColumns.BODY + " TEXT, "
                        + ArticlesModelColumns.IMAGE_URL + " TEXT, "
                        + ArticlesModelColumns.CATEGORY_ID + " INTEGER, "
                        + ArticlesModelColumns.AUTHOR_ID + " INTEGER "
                        + ");";
                db.execSQL(createArticlesTable);
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