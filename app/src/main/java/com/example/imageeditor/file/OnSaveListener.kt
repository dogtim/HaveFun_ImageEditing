package com.example.imageeditor.file

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