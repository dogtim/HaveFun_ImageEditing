package com.example.imageeditor.core

import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.example.imageeditor.R
import com.example.imageeditor.core.view.PhotoEditorView

internal class Emoji(
    photoEditorView: PhotoEditorView
) : Graphic(
    context = photoEditorView.context,
    layoutId = R.layout.view_photo_editor_text
) {
    private var txtEmoji: TextView? = null
    fun buildView(emojiTypeface: Typeface?, emojiName: String?) {
        txtEmoji?.apply {
            if (emojiTypeface != null) {
                typeface = emojiTypeface
            }
            textSize = 56f
            text = emojiName
        }
    }

    override fun setupView(rootView: View) {
        txtEmoji = rootView.findViewById(R.id.tvPhotoEditorText)
        txtEmoji?.run {
            gravity = Gravity.CENTER
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }

}