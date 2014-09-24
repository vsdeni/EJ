package com.vsdeni.ejru;

import android.app.Application;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by denis on 9/24/14.
 */
public class TitleTextView extends TextView {

    public TitleTextView(Context context) {
        super(context);
        init();
    }

    public TitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        int start = 0;
        SpannableString spannableString = new SpannableString(text);
        for (int i = 0; i < getLineCount(); i++) {
            int end = getLayout().getLineStart(i);
            if (end > start) {
                spannableString.setSpan(new RoundedBackgroundSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = end + 1;
            }
        }
        super.setText(spannableString, type);
    }

    private void init() {

//        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/j55auj00.ttf");
//        setTypeface(tf);
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
            paint.setColor(getResources().getColor(android.R.color.black));
            canvas.drawText(text, start, end, x, y, paint);
        }

        private float measureText(Paint paint, CharSequence text, int start, int end) {
            return paint.measureText(text, start, end);
        }
    }
}
