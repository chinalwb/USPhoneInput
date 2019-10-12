//package com.martingale.pact.phoneinputlib
//
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.text.Layout
//import android.text.Spanned
//import android.text.style.LeadingMarginSpan
//import android.util.Log
//
//class FloatingTextSpan(private val mText: String, private val mPaint: Paint) : LeadingMarginSpan {
//
//    // The extra space between label and real text
//    // TODO should be configurable with unit of dp
//    var LEADING_MARGIN = 10
//
//    override fun getLeadingMargin(first: Boolean): Int {
//        if (first) {
//            val width = mPaint.measureText(mText).toInt()
//            return LEADING_MARGIN + width
//        }
//        return 0
//    }
//
//    override fun drawLeadingMargin(
//        c: Canvas, p: Paint, x: Int, dir: Int, top: Int,
//        baseline: Int, bottom: Int, text: CharSequence, start: Int, end: Int,
//        first: Boolean, l: Layout
//    ) {
//        if ((text as Spanned).getSpanStart(this) == start) {
//            c.drawText(
//                mText,
//                (x + dir).toFloat(),
//                baseline.toFloat(),
//                mPaint
//            )
//        }
//    }
//}