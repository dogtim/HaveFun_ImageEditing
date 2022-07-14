package com.example.imageeditor.file

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.Throws
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FileSaveHelper(private val mContentResolver: ContentResolver) : LifecycleObserver {
    private val executor: ExecutorService? = Executors.newSingleThreadExecutor()
    private val fileCreatedResult: MutableLiveData<FileMeta> = MutableLiveData()
    private var resultListener: OnFileCreateResult? = null

    val status = MutableLiveData<PhotoSaverStatus>()
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
            ScalingUtility().createScaledBitmap(
                bitmap,
                (view.width * scale).toInt(),
                (view.height * scale).toInt(),
                ScalingUtility.ScalingLogic.FIT
            )
        }
    }

    /**
     * Save the Bitmap from indicated view and place to given path
     *
     * @param path path on which image to be saved
     * @param view generate the bitmap from this view
     */
    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun save(path: String, view: View, result: () -> ContentResolver) {
        status.value = PhotoSaverStatus.LOADING
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
            notifyThatFileIsNowPubliclyAvailable(
                result()
            )
            status.value = PhotoSaverStatus.DONE
        }.onFailure {
            it.printStackTrace()
            status.value = PhotoSaverStatus.ERROR
        }
    }

    private val observer = Observer { fileMeta: FileMeta ->
        if (resultListener != null) {
            resultListener!!.onFileCreateResult(
                fileMeta.isCreated,
                fileMeta.filePath,
                fileMeta.error,
                fileMeta.uri
            )
        }
    }

    constructor(activity: AppCompatActivity) : this(activity.contentResolver) {
        addObserver(activity)
    }

    private fun addObserver(lifecycleOwner: LifecycleOwner) {
        fileCreatedResult.observe(lifecycleOwner, observer)
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun release() {
        executor?.shutdownNow()
    }

    /**
     * The effects of this method are
     * 1- insert new Image File data in MediaStore.Images column
     * 2- create File on Disk.
     *
     * @param fileNameToSave fileName
     * @param listener       result listener
     */
    fun createFile(fileNameToSave: String, listener: OnFileCreateResult?) {
        resultListener = listener
        executor!!.submit {
            var cursor: Cursor? = null
            try {

                // Build the edited image URI for the MediaStore
                val newImageDetails = ContentValues()
                val imageCollection = buildUriCollection(newImageDetails)
                val editedImageUri =
                    getEditedImageUri(fileNameToSave, newImageDetails, imageCollection)

                // Query the MediaStore for the image file path from the image Uri
                cursor = mContentResolver.query(
                    editedImageUri,
                    arrayOf(MediaStore.Images.Media.DATA),
                    null,
                    null,
                    null
                )
                val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                val filePath = cursor.getString(columnIndex)

                // Post the file created result with the resolved image file path
                updateResult(true, filePath, null, editedImageUri, newImageDetails)
            } catch (ex: Exception) {
                ex.printStackTrace()
                updateResult(false, null, ex.message, null, null)
            } finally {
                cursor?.close()
            }
        }
    }

    @Throws(IOException::class)
    private fun getEditedImageUri(
        fileNameToSave: String,
        newImageDetails: ContentValues,
        imageCollection: Uri
    ): Uri {
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileNameToSave)
        val editedImageUri = mContentResolver.insert(imageCollection, newImageDetails)
        val outputStream = mContentResolver.openOutputStream(editedImageUri!!)
        outputStream!!.close()
        return editedImageUri
    }

    @SuppressLint("InlinedApi")
    private fun buildUriCollection(newImageDetails: ContentValues): Uri {
        val imageCollection: Uri
        if (isSdkHigherThan28()) {
            imageCollection = MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
            newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 1)
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        return imageCollection
    }

    @SuppressLint("InlinedApi")
    fun notifyThatFileIsNowPubliclyAvailable(contentResolver: ContentResolver) {
        if (isSdkHigherThan28()) {
            executor!!.submit {
                val value = fileCreatedResult.value
                if (value != null) {
                    value.imageDetails!!.clear()
                    value.imageDetails!!.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(value.uri!!, value.imageDetails, null, null)
                }
            }
        }
    }

    private class FileMeta(
        var isCreated: Boolean, var filePath: String?,
        var uri: Uri?, var error: String?,
        var imageDetails: ContentValues?
    )

    interface OnFileCreateResult {
        /**
         * @param created  whether file creation is success or failure
         * @param filePath filepath on disk. null in case of failure
         * @param error    in case file creation is failed . it would represent the cause
         * @param Uri      Uri to the newly created file. null in case of failure
         */
        fun onFileCreateResult(created: Boolean, filePath: String?, error: String?, uri: Uri?)
    }

    private fun updateResult(
        result: Boolean,
        filePath: String?,
        error: String?,
        uri: Uri?,
        newImageDetails: ContentValues?
    ) {
        fileCreatedResult.postValue(FileMeta(result, filePath, uri, error, newImageDetails))
    }

    companion object {
        fun isSdkHigherThan28(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        }
    }

}