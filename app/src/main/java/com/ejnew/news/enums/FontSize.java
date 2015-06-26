package com.ejnew.news.enums;

import com.ejnew.news.R;

/**
 * Created by Deni on 26.06.2015.
 */
public enum FontSize {
    STANDART(R.string.font_size_standart, 16),
    BIG(R.string.font_size_big, 22),
    VERY_BIG(R.string.font_size_very_big, 28),
    LARGE(R.string.font_size_large, 34);

    FontSize(int titleResId, int size) {
        mTitleResId = titleResId;
        mSize = size;
    }

    private int mTitleResId;
    private int mSize;

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getSize() {
        return mSize;
    }

    public static FontSize getById(int id) {
        for (FontSize fontSize : values()) {
            if (fontSize.ordinal() == id) {
                return fontSize;
            }
        }
        return STANDART;
    }
}
