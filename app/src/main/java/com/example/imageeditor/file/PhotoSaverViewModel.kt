package com.example.imageeditor.file

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

enum class PhotoSaverStatus { LOADING, ERROR, DONE }

class PhotoSaverViewModel : ViewModel() {

    private val _status = MutableLiveData<PhotoSaverStatus>()
    val status: LiveData<PhotoSaverStatus> = _status

    private fun getBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        view.draw(Canvas(bitmap))
        return bitmap
    }

    /**
     * Save the Bitmap from indicated view and place to given path
     *
     * @param path path on which image to be saved
     * @param view generate the bitmap from this view
     */
    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun save(path: String, view: View) {
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
                _status.value = PhotoSaverStatus.DONE
            }.onFailure {
                it.printStackTrace()
                _status.value = PhotoSaverStatus.ERROR
            }
        }

    }
}
