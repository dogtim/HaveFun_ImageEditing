package com.example.imageeditor.file

import android.Manifest
import android.content.ContentResolver
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.imageeditor.core.view.PhotoEditorView
import kotlinx.coroutines.launch

class PhotoSaverViewModel(private val fileSaveHelper: FileSaveHelper)  : ViewModel() {
    val status: LiveData<FileAccessStatus> = fileSaveHelper.status

    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun exportFile(fileName: String, photoEditorView: PhotoEditorView) {
        viewModelScope.launch {
            fileSaveHelper.createFile(fileName, photoEditorView)
        }
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
