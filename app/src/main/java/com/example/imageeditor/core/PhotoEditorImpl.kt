package com.example.imageeditor.core

import android.Manifest
import android.graphics.Typeface
import androidx.annotation.RequiresPermission
import com.example.imageeditor.core.view.OnSaveBitmap
import com.example.imageeditor.core.view.PhotoEditorView
import com.example.imageeditor.file.PhotoSaverTask

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

    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    override fun saveAsFile(
        imagePath: String,
        onSaveListener: PhotoEditor.OnSaveListener
    ) {
        photoEditorView.saveFilter(object : OnSaveBitmap {
            override fun onBitmapReady() {
                val photoSaverTask = PhotoSaverTask(photoEditorView)
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