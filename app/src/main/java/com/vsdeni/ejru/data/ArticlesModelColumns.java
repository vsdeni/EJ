package com.vsdeni.ejru.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Admin on 04.09.2014.
 */
public class ArticlesModelColumns implements BaseColumns {
    public static final Uri URI = Uri.parse("content://" + EjContentProvider.AUTHORITY + "/" + EjContentProvider.ARTICLES_TABLE_NAME);

    public static final String ID = "id";
    public static final String BODY = "body";
    public static final String AUTHOR_ID = "author_id";
    public static final String CATEGORY_ID = "category_id";
    public static final String IMAGE_URL = "image_url";
}
