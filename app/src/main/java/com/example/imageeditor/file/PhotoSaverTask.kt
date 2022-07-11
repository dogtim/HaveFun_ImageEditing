package com.example.imageeditor.file

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.example.imageeditor.core.BoxHelper
import com.example.imageeditor.core.view.OnSaveBitmap
import com.example.imageeditor.core.view.PhotoEditorView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// TODO Make it to WorkManager
@Deprecated("Legacy async solution")
internal class PhotoSaverTask(private val photoEditorView: PhotoEditorView, private val boxHelper: BoxHelper) :
    AsyncTask<String?, String?, PhotoSaverTask.SaveResult>() {
    private var onSaveListener: OnSaveListener? = null
    private var onSaveBitmap: OnSaveBitmap? = null

    fun setOnSaveListener(onSaveListener: OnSaveListener?) {
        this.onSaveListener = onSaveListener
    }

    @Deprecated("Deprecated in Java")
    override fun onPreExecute() {
        super.onPreExecute()
        boxHelper.clear()
    }

    @SuppressLint("MissingPermission")
    override fun doInBackground(vararg inputs: String?): SaveResult {
        // Create a media file name
        return if (inputs.isEmpty()) {
            saveImageAsBitmap()
        } else {
            saveImageInFile(inputs.first().toString())
        }
    }

    private fun saveImageAsBitmap(): SaveResult {
        return SaveResult(null, null, buildBitmap())
    }

    private fun saveImageInFile(mImagePath: String): SaveResult {
        val file = File(mImagePath)
        return try {
            val out = FileOutputStream(file, false)
            val capturedBitmap = buildBitmap()
            capturedBitmap?.compress(
                Bitmap.CompressFormat.PNG,
                100,
                out
            )
            out.flush()
            out.close()
            Log.d(TAG, "Filed Saved Successfully")
            SaveResult(null, mImagePath, null)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "Failed to save File")
            SaveResult(e, mImagePath, null)
        }
    }

    private fun buildBitmap(): Bitmap? {
        return captureView(photoEditorView)
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(saveResult: SaveResult) {
        super.onPostExecute(saveResult)
        if (TextUtils.isEmpty(saveResult.mImagePath)) {
            handleBitmapCallback(saveResult)
        } else {
            handleFileCallback(saveResult)
        }
    }

    private fun handleFileCallback(saveResult: SaveResult) {
        val exception = saveResult.mException
        val imagePath = saveResult.mImagePath
        if (exception == null) {
            assert(imagePath != null)
            onSaveListener?.onSuccess(imagePath!!)
        } else {
            onSaveListener?.onFailure(exception)
        }
    }

    private fun handleBitmapCallback(saveResult: SaveResult) {
        val bitmap = saveResult.mBitmap
        if (bitmap != null) {
            onSaveBitmap?.onBitmapReady()
        } else {
            onSaveBitmap?.onFailure(Exception("Failed to load the bitmap"))
        }
    }

    private fun captureView(view: View?): Bitmap? {
        if (view == null) {
            return null
        }
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    internal class SaveResult(
        val mException: Exception?,
        val mImagePath: String?,
        val mBitmap: Bitmap?
    )

    companion object {
        const val TAG = "PhotoSaverTask"
    }

}