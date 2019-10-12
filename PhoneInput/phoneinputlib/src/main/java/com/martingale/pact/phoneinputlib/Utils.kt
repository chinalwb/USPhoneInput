package com.martingale.pact.phoneinputlib

import android.content.res.Resources
import android.util.TypedValue

class Utils {
    companion object {

        fun dp2px(dp: Float) = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            Resources.getSystem().displayMetrics
        )


        fun sp2px(sp: Float) = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            Resources.getSystem().displayMetrics
        )
    }
}