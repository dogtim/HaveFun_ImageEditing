package com.example.imageeditor.file

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imageeditor.core.view.PhotoEditorView
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


enum class PhotoSaverStatus { LOADING, ERROR, DONE }

class PhotoSaverViewModel : ViewModel() {
    private lateinit var fileSaveHelper: FileSaveHelper

    private val _status = MutableLiveData<PhotoSaverStatus>()
    val status: LiveData<PhotoSaverStatus> = _status
    private val scale = 1.0f
    private fun getBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        view.draw(Canvas(bitmap))

        return if (scale.equals(1.0f)) {
            bitmap
        } else {
            ScalingUtility().createScaledBitmap(bitmap, (view.width * scale).toInt(), (view.height * scale).toInt(), ScalingUtility.ScalingLogic.FIT)
        }
    }

    /**
     * Save the Bitmap from indicated view and place to given path
     *
     * @param path path on which image to be saved
     * @param view generate the bitmap from this view
     */
    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    private fun save(path: String, view: View, result: () -> ContentResolver) {
        viewModelScope.launch {
            _status.value = PhotoSaverStatus.LOADING
            val file = File(path)
            // https://stackoverflow.com/questions/58680028/how-to-make-inappropriate-blocking-method-call-appropriate
            // To replace try, catch with runCatching for figuring out the lint problem
            runCatching {
                val out = FileOutputStream(file, false)
                val capturedBitmap = getBitmap(view)
                capturedBitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    out
                )
                out.flush()
                out.close()
                // You should execute below to make the output into photo content provider
                fileSaveHelper.notifyThatFileIsNowPubliclyAvailable(
                    result()
                )
                _status.value = PhotoSaverStatus.DONE
            }.onFailure {
                it.printStackTrace()
                _status.value = PhotoSaverStatus.ERROR
            }
        }

    }

    fun set(activity: AppCompatActivity) {
        fileSaveHelper = FileSaveHelper(activity)
    }

    fun creatFile(fileName: String, photoEditorView: PhotoEditorView, result: () -> ContentResolver) {
        fileSaveHelper.createFile(fileName, object : FileSaveHelper.OnFileCreateResult {
            @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
            override fun onFileCreateResult(
                created: Boolean,
                filePath: String?,
                error: String?,
                uri: Uri?
            ) {
                if (created && filePath != null) {
                    save(filePath, photoEditorView) {
                        result()
                    }
                }
            }
        })
    }
}
