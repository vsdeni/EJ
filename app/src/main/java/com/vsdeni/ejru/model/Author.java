package com.vsdeni.ejru.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Admin on 06.09.2014.
 */
public class Author {
    @SerializedName("id")
    private int mId;
    @SerializedName("name")
    private String mName;

    private String mAdditionInfo;

    public Author(int id, String name, String additionInfo) {
        mId = id;
        mName = name;
        mAdditionInfo = additionInfo;
    }

    public Author() {
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

    public String getAdditionInfo() {
        return mAdditionInfo;
    }

    public static class List {
        @SerializedName("authors")
        private ArrayList<Author> mAuthors;

        public ArrayList<Author> getAuthors() {
            return mAuthors;
        }
    }
}