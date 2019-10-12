package com.martingale.pact.phoneinputlib;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.util.Log;

public class FloatingTextSpan implements LeadingMarginSpan {

    // The extra space between label and real text
    // TODO should be configurable with unit of dp
    protected int LEADING_MARGIN = 10;

    private String mText;
    private Paint mPaint;

    public FloatingTextSpan(String text, Paint paint) {
        mText = text;
        mPaint = paint;
    }

    public int getLeadingMargin(boolean first) {
        if (first) {
            int width = (int) mPaint.measureText(mText);
            Log.e("XX", "width == " + width);
            return LEADING_MARGIN + width;
        }
        return 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top,
                                  int baseline, int bottom, CharSequence text, int start, int end,
                                  boolean first, Layout l) {

        if (((Spanned) text).getSpanStart(this) == start) {
            c.drawText(
                    mText,
                    x + dir,
                    baseline,
                    mPaint);
        }
    }
}