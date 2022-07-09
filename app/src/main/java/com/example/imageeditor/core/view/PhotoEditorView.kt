package com.example.imageeditor.core.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.RelativeLayout

interface OnSaveBitmap {
    fun onBitmapReady()
    fun onFailure(e: Exception?)
}

class PhotoEditorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    fun saveFilter(onSaveBitmap: OnSaveBitmap) {
        onSaveBitmap.onBitmapReady()
    }

}