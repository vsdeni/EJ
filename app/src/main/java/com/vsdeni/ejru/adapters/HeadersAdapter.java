package com.vsdeni.ejru.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
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
import android.util.Log;
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
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.vsdeni.ejru.R;
import com.vsdeni.ejru.TitleTextView;
import com.vsdeni.ejru.data.HeadersModelColumns;

import java.io.File;
import java.io.IOException;
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
    private int mThumbnailHeight = 300;
    private int mThumbnailWidht = 1020;

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
            String existingUrl = cursor.getString(cursor.getColumnIndex(HeadersModelColumns.THUMBNAIL_URL));

            int id = cursor.getInt(cursor.getColumnIndex(HeadersModelColumns.ID));

            final String thumbnailUrl;
            if (TextUtils.isEmpty(existingUrl)) {
                thumbnailUrl = "http://ej.ru/img/content/Notes/" + id + "/anons/anons160.jpg";
            } else {
                thumbnailUrl = existingUrl;
            }

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

            viewHolder.blurView.setVisibility(View.GONE);

            File file = ImageLoader.getInstance().getDiskCache().get(thumbnailUrl);
            if (file != null && file.exists()) {
                Drawable d = Drawable.createFromPath(file.getPath());
                viewHolder.blurView.setBackgroundDrawable(d);
                viewHolder.blurView.setVisibility(View.VISIBLE);
            } else {
                ImageLoader.getInstance().loadImage(thumbnailUrl, new ImageSize(mThumbnailWidht, mThumbnailHeight), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        new BlurAsyncTask(thumbnailUrl, loadedImage, viewHolder.blurView).execute();
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }

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
        String mUrl;

        public BlurAsyncTask(String url, Bitmap source, View view) {
            mSource = source;
            mView = view;
            mUrl = url;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected Bitmap doInBackground(Void... voids) {
            long startMs = System.currentTimeMillis();
            Bitmap bitmap = ThumbnailUtils.extractThumbnail(mSource, mThumbnailWidht, mThumbnailHeight);
            float scaleFactor = 8;
            float radius = 1;

            Bitmap overlay = Bitmap.createBitmap((int) (mThumbnailWidht / scaleFactor),
                    (int) (mThumbnailHeight / scaleFactor), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(overlay);
            canvas.translate(0, 0);
            canvas.scale(1 / scaleFactor, 1 / scaleFactor);
            Paint paint = new Paint();
            paint.setFlags(Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(bitmap, 0, 0, paint);

            overlay = fastblur(overlay, (int) radius);

            try {
                ImageLoader.getInstance().getDiskCache().save(mUrl, overlay);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i("blur", String.valueOf(System.currentTimeMillis() - startMs));
            return overlay;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mView.setBackground(new BitmapDrawable(
                    mContext.getResources(), bitmap));
            mView.setVisibility(View.VISIBLE);
            super.onPostExecute(bitmap);
        }

        public Bitmap fastblur(Bitmap sentBitmap, int radius) {

            // Stack Blur v1.0 from
            // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
            //
            // Java Author: Mario Klingemann <mario at quasimondo.com>
            // http://incubator.quasimondo.com
            // created Feburary 29, 2004
            // Android port : Yahel Bouaziz <yahel at kayenko.com>
            // http://www.kayenko.com
            // ported april 5th, 2012

            // This is a compromise between Gaussian Blur and Box blur
            // It creates much better looking blurs than Box Blur, but is
            // 7x faster than my Gaussian Blur implementation.
            //
            // I called it Stack Blur because this describes best how this
            // filter works internally: it creates a kind of moving stack
            // of colors whilst scanning through the image. Thereby it
            // just has to add one new block of color to the right side
            // of the stack and remove the leftmost color. The remaining
            // colors on the topmost layer of the stack are either added on
            // or reduced by one, depending on if they are on the right or
            // on the left side of the stack.
            //
            // If you are using this algorithm in your code please add
            // the following line:
            //
            // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

            if (radius < 1) {
                return (null);
            }

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            int[] pix = new int[w * h];
            Log.e("pix", w + " " + h + " " + pix.length);
            bitmap.getPixels(pix, 0, w, 0, 0, w, h);

            int wm = w - 1;
            int hm = h - 1;
            int wh = w * h;
            int div = radius + radius + 1;

            int r[] = new int[wh];
            int g[] = new int[wh];
            int b[] = new int[wh];
            int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
            int vmin[] = new int[Math.max(w, h)];

            int divsum = (div + 1) >> 1;
            divsum *= divsum;
            int dv[] = new int[256 * divsum];
            for (i = 0; i < 256 * divsum; i++) {
                dv[i] = (i / divsum);
            }

            yw = yi = 0;

            int[][] stack = new int[div][3];
            int stackpointer;
            int stackstart;
            int[] sir;
            int rbs;
            int r1 = radius + 1;
            int routsum, goutsum, boutsum;
            int rinsum, ginsum, binsum;

            for (y = 0; y < h; y++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                for (i = -radius; i <= radius; i++) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))];
                    sir = stack[i + radius];
                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);
                    rbs = r1 - Math.abs(i);
                    rsum += sir[0] * rbs;
                    gsum += sir[1] * rbs;
                    bsum += sir[2] * rbs;
                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }
                }
                stackpointer = radius;

                for (x = 0; x < w; x++) {

                    r[yi] = dv[rsum];
                    g[yi] = dv[gsum];
                    b[yi] = dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm);
                    }
                    p = pix[yw + vmin[x]];

                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[(stackpointer) % div];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi++;
                }
                yw += w;
            }
            for (x = 0; x < w; x++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                yp = -radius * w;
                for (i = -radius; i <= radius; i++) {
                    yi = Math.max(0, yp) + x;

                    sir = stack[i + radius];

                    sir[0] = r[yi];
                    sir[1] = g[yi];
                    sir[2] = b[yi];

                    rbs = r1 - Math.abs(i);

                    rsum += r[yi] * rbs;
                    gsum += g[yi] * rbs;
                    bsum += b[yi] * rbs;

                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }

                    if (i < hm) {
                        yp += w;
                    }
                }
                yi = x;
                stackpointer = radius;
                for (y = 0; y < h; y++) {
                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w;
                    }
                    p = x + vmin[y];

                    sir[0] = r[p];
                    sir[1] = g[p];
                    sir[2] = b[p];

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[stackpointer];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi += w;
                }
            }

            Log.e("pix", w + " " + h + " " + pix.length);
            bitmap.setPixels(pix, 0, w, 0, 0, w, h);

            return (bitmap);
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
        TextView tvThumbnail;
        View blurView;
    }
}