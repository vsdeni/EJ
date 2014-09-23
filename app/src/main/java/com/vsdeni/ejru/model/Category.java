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

    public Category(int id, String name){
        mId = id;
        mName = name;
    }

    public Category(){
    }

    public static class List {
        @SerializedName("categories")
        private ArrayList<Category> mCategories;

        public ArrayList<Category> getCategories() {
            return mCategories;
        }
    }
}
