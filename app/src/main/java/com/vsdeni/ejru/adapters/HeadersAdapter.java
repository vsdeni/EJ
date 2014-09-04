package com.vsdeni.ejru.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.vsdeni.ejru.R;
import com.vsdeni.ejru.data.HeadersModelColumns;

/**
 * Created by Admin on 05.09.2014.
 */
public class HeadersAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;

    public HeadersAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

            tvName.setText(cursor.getString(cursor.getColumnIndex(HeadersModelColumns.NAME)));
        }
    }
}
