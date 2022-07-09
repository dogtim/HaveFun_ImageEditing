package com.example.imageeditor.core

import android.graphics.Typeface
import com.example.imageeditor.core.view.PhotoEditorView

class PhotoEditorImpl(
    builder: PhotoEditor.Builder
) : PhotoEditor {
    private val photoEditorView: PhotoEditorView = builder.photoEditorView
    private val graphicManager: GraphicManager = GraphicManager(builder.photoEditorView)

    override fun addEmoji(emojiTypeface: Typeface?, emojiName: String?) {
        val emoji = Emoji(photoEditorView)
        emoji.buildView(emojiTypeface, emojiName)
        graphicManager.addView(emoji)
    }

}