package com.vsdeni.ejru.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vsdeni.ejru.R;
import com.vsdeni.ejru.TitleTextView;
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
    private int mThumbnailHeight;
    private int mThumbnailWidht;

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
        viewHolder.tvAuthor = (TextView) view.findViewById(R.id.tv_header_author);
        viewHolder.tvDate = (TextView) view.findViewById(R.id.tv_header_date);
        viewHolder.tvName = (TextView) view.findViewById(R.id.tv_header_name);
        viewHolder.tvSpoiler = (TextView) view.findViewById(R.id.tv_spoiler);
        viewHolder.ivThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
        viewHolder.tvThumbnail = (TextView) view.findViewById(R.id.tv_thumbnail);
        viewHolder.blurView = view.findViewById(R.id.blur);
        view.setTag(viewHolder);
        return view;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blur(Bitmap bkg, View view) {

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null) {
            final ViewHolder viewHolder = (ViewHolder) view.getTag();
            String thumbnailUrl = cursor.getString(cursor.getColumnIndex(HeadersModelColumns.THUMBNAIL_URL));
            int id = cursor.getInt(cursor.getColumnIndex(HeadersModelColumns.ID));

            final String name = cursor.getString(cursor.getColumnIndex(HeadersModelColumns.NAME));

            viewHolder.tvThumbnail.setText(name);

            viewHolder.tvThumbnail.post(new Runnable() {
                @Override
                public void run() {
                    int start = 0;
                    int end = 0;
                    int count = viewHolder.tvThumbnail.getLineCount();

                    if (count == 1) {
                        SpannableString spannableString = new SpannableString(name + " ");
                        spannableString.setSpan(new RoundedBackgroundSpan(), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder.tvThumbnail.setText(spannableString);
                    } else {
                        Layout layout = viewHolder.tvThumbnail.getLayout();
                        SpannableStringBuilder spannableString = new SpannableStringBuilder();
                        for (int i = 0; i < count; i++) {
                            end = layout.getLineEnd(i);
                            spannableString.append(name.substring(start, end));

                            int spanStart = start;
                            int spanEnd = end;
                            if (i > 0) {
                                spanStart += i;
                                spanEnd += i;
                            }
                            spannableString.setSpan(new RoundedBackgroundSpan(), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (i < count - 1) {
                                spannableString.append("\n");
                            }
                            start = end;
                        }
                        viewHolder.tvThumbnail.setText(spannableString);
                    }
                }
            });

            if (TextUtils.isEmpty(thumbnailUrl)) {
                thumbnailUrl = "http://ej.ru/img/content/Notes/" + id + "/anons/anons160.jpg";
            }

            if (mThumbnailHeight == 0) {
                viewHolder.blurView.post(new Runnable() {
                    @Override
                    public void run() {
                        mThumbnailHeight = viewHolder.blurView.getMeasuredHeight();
                        mThumbnailWidht = viewHolder.blurView.getMeasuredWidth();
                    }
                });
                return;
            }

            ImageLoader.getInstance().loadImage(thumbnailUrl, new ImageSize(mThumbnailWidht, mThumbnailHeight), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    viewHolder.ivThumbnail.setImageBitmap(loadedImage);
                    viewHolder.ivThumbnail.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            viewHolder.ivThumbnail.getViewTreeObserver().removeOnPreDrawListener(this);
                            viewHolder.ivThumbnail.buildDrawingCache();
                            new BlurAsyncTask(viewHolder.ivThumbnail.getDrawingCache(), viewHolder.blurView).execute();
                            return true;
                        }
                    });
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            long timestamp = cursor.getLong(cursor.getColumnIndex(HeadersModelColumns.TIMESTAMP));
            mCalendar.setTimeInMillis(timestamp * 1000);
            viewHolder.tvDate.setText(mDateFormat.format(mCalendar.getTime()));
            viewHolder.tvName.setText(name);
            viewHolder.tvAuthor.setText(cursor.getString(cursor.getColumnIndex("author_name")));
            viewHolder.tvSpoiler.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(HeadersModelColumns.SPOILER))));
        }
    }

    private class BlurAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        View mView;
        Bitmap mSource;

        public BlurAsyncTask(Bitmap source, View view) {
            mSource = source;
            mView = view;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected Bitmap doInBackground(Void... voids) {
            long startMs = System.currentTimeMillis();
            float radius = 10;
            Bitmap overlay = Bitmap.createBitmap((int) (mThumbnailWidht),
                    (int) (mThumbnailHeight), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(overlay);
            canvas.translate(0, 0);
            canvas.drawBitmap(mSource, 0, 0, null);

            RenderScript rs = RenderScript.create(mContext);
            Allocation overlayAlloc = Allocation.createFromBitmap(
                    rs, overlay);
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                    rs, overlayAlloc.getElement());

            blur.setInput(overlayAlloc);
            blur.setRadius(radius);
            blur.forEach(overlayAlloc);
            overlayAlloc.copyTo(overlay);

            rs.destroy();
            return overlay;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mView.setBackground(new BitmapDrawable(
                    mContext.getResources(), bitmap));
            super.onPostExecute(bitmap);
        }
    }

    public class RoundedBackgroundSpan extends ReplacementSpan {

        @Override
        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            return Math.round(measureText(paint, charSequence, i, i2));
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);
            paint.setColor(Color.parseColor("#BBFFFFFF"));
            canvas.drawRect(rect, paint);
            paint.setColor(mContext.getResources().getColor(android.R.color.black));
            canvas.drawText(text, start, end, x, y, paint);
        }

        private float measureText(Paint paint, CharSequence text, int start, int end) {
            return paint.measureText(text, start, end);
        }
    }

    private class ViewHolder {
        TextView tvAuthor;
        TextView tvDate;
        TextView tvName;
        TextView tvSpoiler;
        ImageView ivThumbnail;
        TextView tvThumbnail;
        View blurView;
    }
}