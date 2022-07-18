package com.example.imageeditor.core

import android.graphics.Typeface
import android.net.Uri
import com.example.imageeditor.core.graphic.Emoji
import com.example.imageeditor.core.graphic.Graphic
import com.example.imageeditor.core.graphic.GraphicManager
import com.example.imageeditor.core.graphic.PhotoImage
import com.example.imageeditor.core.listener.MultiTouchListener
import com.example.imageeditor.core.view.PhotoEditorView

class PhotoEditor (
    val photoEditorView: PhotoEditorView
)  {
    val graphicManager: GraphicManager = GraphicManager(photoEditorView, PhotoEditorViewState())

    fun addEmoji(emojiTypeface: Typeface?, emojiName: String?) {
        val emoji = Emoji(photoEditorView, getMultiTouchListener(), graphicManager)
        emoji.buildView(emojiTypeface, emojiName)
        addToEditor(emoji)
    }

    fun addImage(uri: Uri) {
        val photoImage = PhotoImage(photoEditorView, getMultiTouchListener(), graphicManager)
        photoImage.buildView(uri)
        addToEditor(photoImage)
    }

    private fun addToEditor(graphic: Graphic) {
        graphicManager.clear()
        graphicManager.addView(graphic)
        // Change the in-focus view
        graphicManager.viewState.currentSelectedView = graphic.rootView
    }

    private fun getMultiTouchListener(): MultiTouchListener {
        return MultiTouchListener(
            photoEditorView,
            graphicManager.viewState
        )
    }

    fun undo(): Boolean {
        return graphicManager.undoView()
    }

    fun redo(): Boolean {
        return graphicManager.redoView()
    }

}