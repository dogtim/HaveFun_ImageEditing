package com.example.imageeditor.core.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.children
import com.example.imageeditor.R
import com.example.imageeditor.core.data.Emoji

class PhotoEditorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    fun createEmojiList(): List<Emoji> {
        val list = mutableListOf<Emoji>()
        children.forEach {
            it.findViewById<TextView>(R.id.text_photo_editor)?.run {
                val data = Emoji(text.toString())
                data.left = it.x.toInt()
                data.top = it.y.toInt()
                data.rotation = it.rotation
                data.scaleX = it.scaleX
                data.scaleY = it.scaleY
                list.add(data)
            }
        }
        return list
    }
}