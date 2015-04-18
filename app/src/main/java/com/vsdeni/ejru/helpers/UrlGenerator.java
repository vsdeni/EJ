package com.vsdeni.ejru.helpers;

import android.content.Context;

import com.vsdeni.ejru.Consts;
import com.vsdeni.ejru.R;

/**
 * Created by Denis on 15.04.2015.
 */
public class UrlGenerator {
    public static String forImage(Context context, int articleId, boolean small) {
        if (small) {
            return context.getString(R.string.image_url_small, Consts.BASE_URL, articleId);
        } else {
            return context.getString(R.string.image_url, Consts.BASE_URL, articleId);
        }
    }

    public static String forArticle(Context context, int articleId) {
        return context.getString(R.string.article_url, Consts.BASE_URL, articleId);
    }

    public static String forAuthor(Context context, int authorId) {
        return context.getString(R.string.author_url, Consts.BASE_URL, authorId);
    }
}
