package com.example.imageeditor.core

import android.net.Uri
import com.example.imageeditor.core.data.EmojiData
import com.example.imageeditor.core.graphic.EmojiGraphic
import com.example.imageeditor.core.graphic.Graphic
import com.example.imageeditor.core.graphic.GraphicManager
import com.example.imageeditor.core.graphic.PhotoGraphic
import com.example.imageeditor.core.view.PhotoEditorView

class PhotoEditor(
    val photoEditorView: PhotoEditorView
) {
    val graphicManager: GraphicManager = GraphicManager(photoEditorView)

    fun addEmoji(emojiData: EmojiData) {
        val context = photoEditorView.context
        val emoji = EmojiGraphic(context, graphicManager, emojiData)
        addToEditor(emoji)
    }

    fun addImage(uri: Uri) {
        val context = photoEditorView.context
        val photoImage = PhotoGraphic(context, graphicManager)
        photoImage.buildView(uri)
        addToEditor(photoImage)
    }

    private fun addToEditor(graphic: Graphic) {
        graphicManager.clearAppearance()
        graphicManager.addView(graphic)
        // Change the in-focus view
        graphicManager.viewState.selectedView = graphic.rootView
    }

    fun undo(): Boolean {
        return graphicManager.undoView()
    }

    fun redo(): Boolean {
        return graphicManager.redoView()
    }

    fun clear() {
        graphicManager.clear()
    }

}