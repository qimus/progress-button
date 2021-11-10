package ru.den.progressbutton.utils

import android.graphics.Paint

fun Paint.getTextWidth(text: String): Float {
    val arr = FloatArray(text.length)
    getTextWidths(text, arr)
    return arr.sum()
}