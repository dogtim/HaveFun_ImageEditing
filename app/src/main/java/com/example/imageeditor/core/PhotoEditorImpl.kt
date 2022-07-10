package com.example.imageeditor.core

import android.Manifest
import android.graphics.Typeface
import android.net.Uri
import androidx.annotation.RequiresPermission
import com.example.imageeditor.core.listener.MultiTouchListener
import com.example.imageeditor.core.view.OnSaveBitmap
import com.example.imageeditor.core.view.PhotoEditorView
import com.example.imageeditor.file.PhotoSaverTask

class PhotoEditorImpl(
    builder: PhotoEditor.Builder
) : PhotoEditor {
    private val photoEditorView: PhotoEditorView = builder.photoEditorView
    private val viewState: PhotoEditorViewState = PhotoEditorViewState()
    private val graphicManager: GraphicManager = GraphicManager(builder.photoEditorView, viewState)
    private val boxHelper: BoxHelper = BoxHelper(builder.photoEditorView, viewState)

    override fun addEmoji(emojiTypeface: Typeface?, emojiName: String?) {
        val emoji = Emoji(photoEditorView, viewState, getMultiTouchListener(), graphicManager)
        emoji.buildView(emojiTypeface, emojiName)
        addToEditor(emoji)
    }

    override fun addImage(uri: Uri) {
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

    override fun undo(): Boolean {
        return graphicManager.undoView()
    }

    override fun redo(): Boolean {
        return graphicManager.redoView()
    }

    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    override fun saveAsFile(
        imagePath: String,
        onSaveListener: PhotoEditor.OnSaveListener
    ) {
        photoEditorView.saveFilter(object : OnSaveBitmap {
            override fun onBitmapReady() {
                val photoSaverTask = PhotoSaverTask(photoEditorView, boxHelper)
                photoSaverTask.setOnSaveListener(onSaveListener)
                photoSaverTask.execute(imagePath)
            }

            override fun onFailure(e: Exception?) {
                e?.run {
                    onSaveListener.onFailure(this)
                }
            }
        })
    }

}