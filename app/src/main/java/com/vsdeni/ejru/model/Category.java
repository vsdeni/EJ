package com.vsdeni.ejru.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Admin on 11.08.2014.
 */
public class Category {
    @SerializedName("id")
    private int mId;

    @SerializedName("name")
    private String mName;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public static class List {
        @SerializedName("categories")
        ArrayList<Category> mCategories;
    }
}
