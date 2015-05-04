package com.ejnew.news.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ejnew.news.R;
import com.ejnew.news.data.CategoriesModelColumns;

/**
 * Created by Admin on 04.09.2014.
 */
public class DrawerAdapter extends CursorAdapter {
    private LayoutInflater mInflater;

    public DrawerAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public DrawerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        TextView view = (TextView) mInflater.inflate(R.layout.row_category, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null) {
            String name = cursor.getString(cursor.getColumnIndex(CategoriesModelColumns.NAME));
            ((TextView) view).setText(name);
        }
    }
}
