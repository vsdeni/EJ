package com.vsdeni.ejru.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Admin on 04.09.2014.
 */
public class CategoriesModelColumns implements BaseColumns {
    public static final Uri URI = Uri.parse("content://" + EjContentProvider.AUTHORITY + "/" + EjContentProvider.CATEGORIES_TABLE_NAME);

    public static final String ID = "id";
    public static final String NAME = "name";
}
