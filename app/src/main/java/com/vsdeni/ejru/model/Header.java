package com.vsdeni.ejru.model;

import com.google.gson.annotations.SerializedName;

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

    public static class List {
        @SerializedName("articles")
        private ArrayList<Header> mHeaders;

        public ArrayList<Header> getHeaders() {
            return mHeaders;
        }
    }
}
