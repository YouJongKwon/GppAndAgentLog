package com.goodpharm.gppandagentlog.util

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView

object TextUtils {

    fun TextView.setCustomText(
        text: String,
        start: Int,
        end: Int,
        color: Int = Color.BLACK,
        isBold: Boolean = false
    ) {
        val builder = SpannableStringBuilder(text)

        var spanStyle = if (isBold) {
            StyleSpan(Typeface.BOLD)
        } else {
            StyleSpan(Typeface.NORMAL)
        }

        if (isBold) {
            builder.setSpan(spanStyle, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        builder.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        this.text = builder
    }
}