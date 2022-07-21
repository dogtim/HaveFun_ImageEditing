package com.example.imageeditor.core

import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import com.example.imageeditor.core.data.EmojiData
import com.example.imageeditor.core.graphic.EmojiGraphic
import com.example.imageeditor.core.graphic.Graphic
import com.example.imageeditor.core.graphic.GraphicManager
import com.example.imageeditor.core.graphic.PhotoImage
import com.example.imageeditor.core.listener.MultiTouchListener
import com.example.imageeditor.core.view.PhotoEditorView

class PhotoEditor (
    val photoEditorView: PhotoEditorView
)  {
    val graphicManager: GraphicManager = GraphicManager(photoEditorView, PhotoEditorViewState())

    fun addEmoji(emojiData: EmojiData) {
        val emoji = EmojiGraphic(photoEditorView, getMultiTouchListener(), graphicManager, emojiData)
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