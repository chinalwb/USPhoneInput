package com.martingale.pact.phoneinputlib

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText


const val ZERO_WIDTH_SPACE_STR = "\u200B"
const val FLOATING_LABEL = "+1"

class PhoneInputEditText(context: Context, val attributeSet: AttributeSet) : AppCompatEditText(context, attributeSet) {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mSize = Utils.dp2px(20f)
    private var mColor = Color.BLACK

    init {
        // Read font size from attribute set
        // and font color
        readAttrs()
        initPaint()
        var floatingLabel = getFloatingLabel()
        setText(floatingLabel)
        initListener()
    }

    private fun readAttrs() {
        val t = context.obtainStyledAttributes(attributeSet, R.styleable.PhoneInputEditText, 0, 0)
        mSize = t.getDimension(R.styleable.PhoneInputEditText_pietSize, mSize)
        mColor = t.getColor(R.styleable.PhoneInputEditText_pietColor, mColor)
        t.recycle()
    }

    private fun initPaint() {
        mPaint.textSize = mSize
        mPaint.color = mColor
    }

    private fun getFloatingLabel(): SpannableStringBuilder {
        val ssb = SpannableStringBuilder(ZERO_WIDTH_SPACE_STR)
        ssb.setSpan(FloatingTextSpan(FLOATING_LABEL, mPaint), 0, ssb.length - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        return ssb
    }

    var lastLen = 0
    private fun initListener() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO
            }

            override fun afterTextChanged(s: Editable?) {
                // TODO
                if (lastLen == s?.length) {
                    return
                }
                var input = s.toString().replace("\\(", "").replace("\\)", "").replace("\\-", "")
                var phone: String
                phone = when {
                    input.length <= 3 -> formatFirstPart(input)
                    input.length <= 6 -> formatSecondPart(input)
                    else -> formatThirdPart(input)
                }

                var ssb = getFloatingLabel()
                ssb.append(phone)
                lastLen = ssb.length

                setText(ssb)
                setSelection(ssb.length - 1)
            }
        })
    }

    private fun formatFirstPart(input: String): String {
        return input.replaceFirst("(\\d+)", "(\$1")
    }

    private fun formatSecondPart(input: String): String {
        return input.replaceFirst("(\\d{3})(\\d+)", "(\$1)\$2")
    }

    private fun formatThirdPart(input: String): String {
        return input.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "(\$1)\$2-\$3")
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val width = mPaint.measureText(FLOATING_LABEL)
        event?.offsetLocation(width + 10, 0F)
        return super.onTouchEvent(event)
    }
}