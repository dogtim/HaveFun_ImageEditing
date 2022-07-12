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
    private val viewState: PhotoEditorViewState = PhotoEditorViewState()
    private val graphicManager: GraphicManager = GraphicManager(photoEditorView, viewState)
    val boxHelper: BoxHelper = BoxHelper(photoEditorView, viewState)

    fun addEmoji(emojiTypeface: Typeface?, emojiName: String?) {
        val emoji = Emoji(photoEditorView, viewState, getMultiTouchListener(), graphicManager)
        emoji.buildView(emojiTypeface, emojiName)
        addToEditor(emoji)
    }

    fun addImage(uri: Uri) {
        val photoImage = PhotoImage(photoEditorView, getMultiTouchListener(), viewState, graphicManager)
        photoImage.buildView(uri)
        addToEditor(photoImage)
    }

    private fun addToEditor(graphic: Graphic) {
        boxHelper.clear()
        graphicManager.addView(graphic)
        // Change the in-focus view
        viewState.currentSelectedView = graphic.rootView
    }

    private fun getMultiTouchListener(): MultiTouchListener {
        return MultiTouchListener(
            photoEditorView,
            viewState
        )
    }

    fun undo(): Boolean {
        return graphicManager.undoView()
    }

    fun redo(): Boolean {
        return graphicManager.redoView()
    }

}