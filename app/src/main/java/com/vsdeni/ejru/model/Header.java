package com.vsdeni.ejru.model;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;
import com.vsdeni.ejru.data.HeadersModelColumns;

import java.util.ArrayList;

/**
 * Created by Admin on 04.09.2014.
 */
public class Header {
    @SerializedName("id")
    private int mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("author_id")
    private int mAuthorId;

    @SerializedName("timestamp")
    private long mTimestamp;

    private int mCategoryId;

    public static Header toHeader(Cursor cursor) {
        if (cursor != null) {
            Header header = new Header();
            header.setAuthorId(cursor.getInt(cursor.getColumnIndex("a_id")));
            header.setId(cursor.getInt(cursor.getColumnIndex(HeadersModelColumns.ID)));
            header.setName(cursor.getString(cursor.getColumnIndex(HeadersModelColumns.NAME)));
            header.setTimestamp(cursor.getLong(cursor.getColumnIndex(HeadersModelColumns.TIMESTAMP)));
            header.setCategoryId(cursor.getInt(cursor.getColumnIndex(HeadersModelColumns.CATEGORY_ID)));
            return header;
        }
        return null;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getAuthorId() {
        return mAuthorId;
    }

    public void setAuthorId(int authorId) {
        mAuthorId = authorId;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(int categoryId) {
        mCategoryId = categoryId;
    }

    public static class List {
        @SerializedName("articles")
        private ArrayList<Header> mHeaders;

        public ArrayList<Header> getHeaders() {
            return mHeaders;
        }
    }
}
