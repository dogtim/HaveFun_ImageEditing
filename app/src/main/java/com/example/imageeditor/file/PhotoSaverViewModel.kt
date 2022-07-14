package com.example.imageeditor.file

import android.Manifest
import android.content.ContentResolver
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.imageeditor.core.view.PhotoEditorView

enum class PhotoSaverStatus { LOADING, ERROR, DONE }

class PhotoSaverViewModel(private val fileSaveHelper: FileSaveHelper)  : ViewModel() {
    val status: LiveData<PhotoSaverStatus> = fileSaveHelper.status

    fun exportFile(fileName: String, photoEditorView: PhotoEditorView, result: () -> ContentResolver) {
        fileSaveHelper.createFile(fileName, object : FileSaveHelper.OnFileCreateResult {
            @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
            override fun onFileCreateResult(
                created: Boolean,
                filePath: String?,
                error: String?,
                uri: Uri?
            ) {
                if (created && filePath != null) {
                    fileSaveHelper.save(filePath, photoEditorView) {
                        result()
                    }
                }
            }
        })
    }

    class PhotoSaverViewFactor(private val fileSaveHelper: FileSaveHelper) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(PhotoSaverViewModel::class.java)) {
                PhotoSaverViewModel(fileSaveHelper) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
