package com.example.imageeditor.core

import android.Manifest
import android.graphics.Typeface
import androidx.annotation.RequiresPermission
import com.example.imageeditor.core.view.PhotoEditorView

interface PhotoEditor {

    fun addEmoji(emojiTypeface: Typeface?, emojiName: String?)

    /**
     * Save the edited image on given path
     *
     * @param imagePath      path on which image to be saved
     * @param saveSettings   builder for multiple save options [SaveSettings]
     * @param onSaveListener callback for saving image
     * @see OnSaveListener
     */
    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun saveAsFile(
        imagePath: String,
        onSaveListener: OnSaveListener
    )

    class Builder(var photoEditorView: PhotoEditorView) {
        fun build(): PhotoEditor {
            return PhotoEditorImpl(this)
        }
    }

    /**
     * A callback to save the edited image asynchronously
     */
    interface OnSaveListener {
        /**
         * Call when edited image is saved successfully on given path
         *
         * @param imagePath path on which image is saved
         */
        fun onSuccess(imagePath: String)

        /**
         * Call when failed to saved image on given path
         *
         * @param exception exception thrown while saving image
         */
        fun onFailure(exception: Exception)
    }
}