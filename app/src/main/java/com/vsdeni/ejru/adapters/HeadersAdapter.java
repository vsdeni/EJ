package com.vsdeni.ejru.adapters;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vsdeni.ejru.R;
import com.vsdeni.ejru.data.HeadersModelColumns;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Admin on 05.09.2014.
 */
public class HeadersAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    private SimpleDateFormat mDateFormat;
    private Calendar mCalendar;

    public HeadersAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDateFormat = new SimpleDateFormat("d MMM, yyyy", Locale.getDefault());
        mCalendar = Calendar.getInstance(Locale.getDefault());
    }

    public HeadersAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.row_header, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null) {
            TextView tvAuthor = (TextView) view.findViewById(R.id.tv_header_author);
            TextView tvDate = (TextView) view.findViewById(R.id.tv_header_date);
            TextView tvName = (TextView) view.findViewById(R.id.tv_header_name);
            TextView tvSpoiler = (TextView) view.findViewById(R.id.tv_spoiler);
            ImageView ivThumbnail = (ImageView) view.findViewById(R.id.iv_header_thumbnail);


            String thumbnailUrl = cursor.getString(cursor.getColumnIndex(HeadersModelColumns.THUMBNAIL_URL));

            ImageLoader.getInstance().displayImage(thumbnailUrl, ivThumbnail);

            long timestamp = cursor.getLong(cursor.getColumnIndex(HeadersModelColumns.TIMESTAMP));
            mCalendar.setTimeInMillis(timestamp * 1000);
            tvDate.setText(mDateFormat.format(mCalendar.getTime()));
            tvName.setText(cursor.getString(cursor.getColumnIndex(HeadersModelColumns.NAME)));
            tvAuthor.setText(cursor.getString(cursor.getColumnIndex("author_name")));
            tvSpoiler.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(HeadersModelColumns.SPOILER))));
        }
    }
}
