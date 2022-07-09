package com.example.imageeditor.core

import android.graphics.Typeface
import com.example.imageeditor.core.view.PhotoEditorView

interface PhotoEditor {

    fun addEmoji(emojiTypeface: Typeface?, emojiName: String?)

    class Builder(var photoEditorView: PhotoEditorView) {
        fun build(): PhotoEditor {
            return PhotoEditorImpl(this)
        }
    }

}