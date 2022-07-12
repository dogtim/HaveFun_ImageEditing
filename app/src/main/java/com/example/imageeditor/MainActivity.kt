package com.example.imageeditor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.imageeditor.core.PhotoEditor
import com.example.imageeditor.file.FileSaveHelper
import com.example.imageeditor.file.PhotoSaverStatus
import com.example.imageeditor.file.PhotoSaverViewModel
import com.example.imageeditor.fragment.ShapeFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MainActivity : AppCompatActivity(), EditorAdapter.OnEditorSelectedListener,
    ShapeFragment.ShapeListener, View.OnClickListener {
    private lateinit var shapeFragment: ShapeFragment
    private lateinit var photoEditor: PhotoEditor
    private lateinit var fileSaveHelper: FileSaveHelper
    private val viewModel: PhotoSaverViewModel by viewModels()
    private lateinit var loadingView: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shapeFragment = ShapeFragment()
        shapeFragment.setListener(this)

        photoEditor = PhotoEditor(findViewById(R.id.photoEditorView))
        loadingView = findViewById(R.id.progress_loading)
        fileSaveHelper = FileSaveHelper(this)
        initRecycleView()
        initImageView()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.status.observe(this) { status ->
            when (status) {
                PhotoSaverStatus.DONE -> {
                    loadingView.visibility = View.GONE
                    // You should execute below to make the output into photo content provider
                    fileSaveHelper.notifyThatFileIsNowPubliclyAvailable(
                        contentResolver
                    )
                }
                PhotoSaverStatus.ERROR -> {
                    loadingView.visibility = View.GONE
                }
                PhotoSaverStatus.LOADING -> {
                    loadingView.visibility = View.VISIBLE
                    photoEditor.boxHelper.clear()
                }
                else -> {
                    Log.e("MainActivity", "You should check the PhotoSaverStatus problem")
                }
            }
        }
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
                launchGalleryApp()
            }
        }
    }

    private fun launchGalleryApp() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    photoEditor.addImage(it)
                }
            }
        }

    override fun onClick(emojiUnicode: String) {
        photoEditor.addEmoji(null, emojiUnicode)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.image_undo -> photoEditor.undo()
            R.id.image_save -> {
                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show()
                saveImage()
            }
            R.id.image_redo -> photoEditor.redo()
        }
    }

    // TODO the request permission failed
    // TODO Show loading
    // Step 1: Create the URI of Image File
    // Step 2: Generate the image file and save to this URI
    private fun saveImage() {
        loadingView.visibility = View.VISIBLE
        val fileName = System.currentTimeMillis().toString() + ".png"
        val hasStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (hasStoragePermission || FileSaveHelper.isSdkHigherThan28()) {
            fileSaveHelper.createFile(fileName, object : FileSaveHelper.OnFileCreateResult {
                @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
                override fun onFileCreateResult(
                    created: Boolean,
                    filePath: String?,
                    error: String?,
                    uri: Uri?
                ) {
                    if (created && filePath != null) {
                        viewModel.save(filePath, photoEditor.photoEditorView)
                    }
                }
            })
        } else {
            // requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
}