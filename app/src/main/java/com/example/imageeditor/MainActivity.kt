package com.example.imageeditor

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.imageeditor.core.PhotoEditor
import com.example.imageeditor.core.data.Backup
import com.example.imageeditor.core.data.Emoji
import com.example.imageeditor.file.BackupViewModel
import com.example.imageeditor.file.FileAccessStatus
import com.example.imageeditor.file.FileSaveHelper
import com.example.imageeditor.file.PhotoSaverViewModel
import com.example.imageeditor.fragment.ShapeFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import java.io.*

class MainActivity : AppCompatActivity(), EditorAdapter.OnEditorSelectedListener, View.OnClickListener {
    private lateinit var shapeFragment: ShapeFragment
    private lateinit var photoEditor: PhotoEditor

    private val viewModel: PhotoSaverViewModel by viewModels {
        PhotoSaverViewModel.PhotoSaverViewFactor(
            FileSaveHelper(contentResolver)
        )
    }

    private val backViewModel: BackupViewModel by viewModels()

    private lateinit var loadingView: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shapeFragment = ShapeFragment()
        shapeFragment.setListener(object: ShapeFragment.ShapeListener {
            override fun onClick(emojiUnicode: String) {
                photoEditor.addEmoji(Emoji(emojiUnicode))
            }
        })

        photoEditor = PhotoEditor(findViewById(R.id.photoEditorView))
        loadingView = findViewById(R.id.progress_loading)

        initRecycleView()
        initImageView()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.status.observe(this) { status ->
            when (status) {
                FileAccessStatus.DONE -> {
                    loadingView.visibility = View.GONE
                }
                FileAccessStatus.ERROR -> {
                    loadingView.visibility = View.GONE
                }
                FileAccessStatus.LOADING -> {
                    loadingView.visibility = View.VISIBLE
                    photoEditor.graphicManager.clearAppearance()
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
        val imageViewList =  listOf(
            R.id.image_undo, R.id.image_save, R.id.image_redo,
            R.id.image_save_status, R.id.image_clear, R.id.image_restore)
        
        imageViewList.forEach {
            val imageView: ImageView = findViewById(it)
            imageView.setOnClickListener(this)
        }
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

    override fun onClick(view: View) {
        when (view.id) {
            R.id.image_save_status -> {
                val list = mutableListOf<Emoji>()
                photoEditor.photoEditorView.children.forEach {
                    it.findViewById<TextView>(R.id.text_photo_editor)?.run {
                        val data = Emoji(text.toString())
                        data.left = it.x.toInt()
                        data.top = it.y.toInt()
                        data.rotation = it.rotation
                        data.scaleX = it.scaleX
                        data.scaleY = it.scaleY
                        list.add(data)
                    }
                }
                backViewModel.buildJson(list, this)
            }
            R.id.image_clear -> {
                photoEditor.clear()
            }
            R.id.image_restore -> {
                val string = backViewModel.readFromFile(this)
                val gson = Gson()
                val testModel = gson.fromJson(string, Backup::class.java)

                testModel.emojis?.let {
                    it.forEach { emoji ->
                        photoEditor.addEmoji(emoji)
                    }
                }
            }
            R.id.image_undo -> {
                photoEditor.undo()
            }

            R.id.image_save -> {
                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show()
                saveImage()
            }
            R.id.image_redo -> {
                photoEditor.redo()
            }
        }
    }

    // TODO the request permission failed
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
            viewModel.exportFile(fileName, photoEditor.photoEditorView)
        } else {
            // requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

}

