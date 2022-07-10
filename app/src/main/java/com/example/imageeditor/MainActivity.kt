package com.example.imageeditor

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.imageeditor.core.PhotoEditor
import com.example.imageeditor.core.view.PhotoEditorView
import com.example.imageeditor.file.FileSaveHelper
import com.example.imageeditor.fragment.ShapeFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.lang.Exception

class MainActivity : AppCompatActivity(), EditorAdapter.OnEditorSelectedListener,
    ShapeFragment.ShapeListener, View.OnClickListener {
    private lateinit var shapeFragment: ShapeFragment
    var photoEditor: PhotoEditor? = null
    private var photoEditorView: PhotoEditorView? = null
    private var fileSaveHelper: FileSaveHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shapeFragment = ShapeFragment()
        shapeFragment.setListener(this)
        photoEditorView = findViewById(R.id.photoEditorView)

        photoEditor = photoEditorView?.run {
            PhotoEditor.Builder(this)
                .build() // build photo editor sdk
        }
        fileSaveHelper = FileSaveHelper(this)
        initRecycleView()
        initImageView()
    }

    private fun initRecycleView() {
        val recyclerView: RecyclerView = findViewById(R.id.recycler_editing_tools)
        recyclerView.adapter = EditorAdapter(this)
    }

    private fun initImageView() {
        val undoImageView: ImageView = findViewById(R.id.image_undo)
        undoImageView.setOnClickListener(this)
        val saveImageView: ImageView = findViewById(R.id.image_save)
        saveImageView.setOnClickListener(this)
        val redoImageView: ImageView = findViewById(R.id.image_redo)
        redoImageView.setOnClickListener(this)
    }

    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment) {
        if (fragment.isAdded) {
            return
        }
        fragment.show(supportFragmentManager, fragment.tag)
    }

    override fun onSelected(toolType: ToolType) {
        when (toolType) {
            ToolType.SHAPE -> {
                showBottomSheetDialogFragment(shapeFragment)
            }

            ToolType.IMAGE -> {
                // TODO Create the Image Browser which can select photo from local storage
            }
        }
    }

    override fun onClick(emojiUnicode: String) {
        photoEditor?.addEmoji(null, emojiUnicode)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.image_undo -> photoEditor?.undo()
            R.id.image_save -> {
                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show()
                saveImage()
            }
            R.id.image_redo -> photoEditor?.redo()
        }
    }

    // TODO the request permission failed
    // TODO Show loading
    // Step 1: Create the URI of Image File
    // Step 2: Generate the image file and save to this URI
    private fun saveImage() {
        val fileName = System.currentTimeMillis().toString() + ".png"
        val hasStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (hasStoragePermission || FileSaveHelper.isSdkHigherThan28()) {
            fileSaveHelper?.createFile(fileName, object : FileSaveHelper.OnFileCreateResult {

                @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
                override fun onFileCreateResult(
                    created: Boolean,
                    filePath: String?,
                    error: String?,
                    uri: Uri?
                ) {
                    if (created && filePath != null) {

                        photoEditor?.saveAsFile(
                            filePath,
                            object : PhotoEditor.OnSaveListener {
                                override fun onSuccess(imagePath: String) {
                                    fileSaveHelper?.notifyThatFileIsNowPubliclyAvailable(
                                        contentResolver
                                    )
                                    // hideLoading()
                                    // showSnackbar("Image Saved Successfully")
                                    // photoEditor?.source?.setImageURI(uri)
                                }

                                override fun onFailure(exception: Exception) {
                                     // hideLoading()
                                    // showSnackbar("Failed to save Image")
                                }
                            })
                    } else {
                        // hideLoading()
                        // error?.let { showSnackbar(error) }
                    }
                }
            })
        } else {
            // requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
}