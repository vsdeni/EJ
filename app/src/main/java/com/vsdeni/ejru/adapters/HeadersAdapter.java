package com.vsdeni.ejru.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vsdeni.ejru.Consts;
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
    private Context mContext;

    public HeadersAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDateFormat = new SimpleDateFormat("d MMM, yyyy", Locale.getDefault());
        mCalendar = Calendar.getInstance(Locale.getDefault());
        mContext = context;
    }

    public HeadersAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.row_header, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.author = (TextView) view.findViewById(R.id.author);
        viewHolder.date = (TextView) view.findViewById(R.id.date);
        viewHolder.spoiler = (TextView) view.findViewById(R.id.spoiler);
        viewHolder.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        viewHolder.title = (TextView) view.findViewById(R.id.title);
        viewHolder.category = (TextView) view.findViewById(R.id.category);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null) {
            final ViewHolder viewHolder = (ViewHolder) view.getTag();
            final int id = cursor.getInt(cursor.getColumnIndex(HeadersModelColumns.ID));
            String thumbnailUrl = Consts.BASE_URL + "/img/content/Notes/" + id + "/anons/anons350.jpg";

            ImageLoader.getInstance().displayImage(thumbnailUrl, viewHolder.thumbnail, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    viewHolder.thumbnail.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    viewHolder.thumbnail.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    viewHolder.thumbnail.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            long timestamp = cursor.getLong(cursor.getColumnIndex(HeadersModelColumns.TIMESTAMP));
            mCalendar.setTimeInMillis(timestamp * 1000);
            viewHolder.date.setText(mDateFormat.format(mCalendar.getTime()));
            viewHolder.category.setText(cursor.getString(cursor.getColumnIndex("category_name")));
            viewHolder.author.setText(cursor.getString(cursor.getColumnIndex("author_name")));
            viewHolder.spoiler.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(HeadersModelColumns.SPOILER))));
            viewHolder.title.setText(cursor.getString(cursor.getColumnIndex(HeadersModelColumns.NAME)));
        }
    }

    private class ViewHolder {
        TextView author;
        TextView date;
        TextView spoiler;
        ImageView thumbnail;
        TextView category;
        TextView title;
    }
}