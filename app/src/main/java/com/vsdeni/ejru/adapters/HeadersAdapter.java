package com.vsdeni.ejru.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
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
        return view;
    }

    private void applyBlur(final ImageView imageView, final TextView textView) {
        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                imageView.buildDrawingCache();

                Bitmap bitmap = imageView.getDrawingCache();
                blur(bitmap, textView);
                return true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float radius = 20;
        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()),
                (int) (view.getMeasuredHeight()), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft(), -view.getTop());
        canvas.drawBitmap(bkg, 0, 0, null);

        RenderScript rs = RenderScript.create(mContext);
        Allocation overlayAlloc = Allocation.createFromBitmap(
                rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                rs, overlayAlloc.getElement());

        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);

        view.setBackground(new BitmapDrawable(
                mContext.getResources(), overlay));

        rs.destroy();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null) {
            TextView tvAuthor = (TextView) view.findViewById(R.id.tv_header_author);
            TextView tvDate = (TextView) view.findViewById(R.id.tv_header_date);
            TextView tvName = (TextView) view.findViewById(R.id.tv_header_name);
            TextView tvSpoiler = (TextView) view.findViewById(R.id.tv_spoiler);
            final ImageView ivThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
            final TextView tvThumbnail = (TextView) view.findViewById(R.id.tv_thumbnail);

            String thumbnailUrl = cursor.getString(cursor.getColumnIndex(HeadersModelColumns.THUMBNAIL_URL));
            int id = cursor.getInt(cursor.getColumnIndex(HeadersModelColumns.ID));

            final String name = cursor.getString(cursor.getColumnIndex(HeadersModelColumns.NAME));

            tvThumbnail.setText(name);

            //if (TextUtils.isEmpty(thumbnailUrl)){
            thumbnailUrl = "http://ej.ru/img/content/Notes/" + id + "/anons/anons160.jpg";
            //}

            ImageLoader.getInstance().displayImage(thumbnailUrl, ivThumbnail);
            ImageLoader.getInstance().loadImage(thumbnailUrl, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ivThumbnail.setImageBitmap(loadedImage);
                    applyBlur(ivThumbnail, tvThumbnail);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            long timestamp = cursor.getLong(cursor.getColumnIndex(HeadersModelColumns.TIMESTAMP));
            mCalendar.setTimeInMillis(timestamp * 1000);
            tvDate.setText(mDateFormat.format(mCalendar.getTime()));
            tvName.setText(name);
            tvAuthor.setText(cursor.getString(cursor.getColumnIndex("author_name")));
            tvSpoiler.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(HeadersModelColumns.SPOILER))));
        }
    }
}