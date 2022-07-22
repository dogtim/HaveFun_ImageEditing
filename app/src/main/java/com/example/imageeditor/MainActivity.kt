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
import com.example.imageeditor.file.FileSaveHelper
import com.example.imageeditor.file.PhotoSaverStatus
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
                PhotoSaverStatus.DONE -> {
                    loadingView.visibility = View.GONE
                }
                PhotoSaverStatus.ERROR -> {
                    loadingView.visibility = View.GONE
                }
                PhotoSaverStatus.LOADING -> {
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
        val undoImageView: ImageView = findViewById(R.id.image_undo)
        undoImageView.setOnClickListener(this)
        val saveImageView: ImageView = findViewById(R.id.image_save)
        saveImageView.setOnClickListener(this)
        val redoImageView: ImageView = findViewById(R.id.image_redo)
        redoImageView.setOnClickListener(this)
        val saveStatusImageView: ImageView = findViewById(R.id.image_save_status)
        saveStatusImageView.setOnClickListener(this)
        val clearImageView: ImageView = findViewById(R.id.image_clear)
        clearImageView.setOnClickListener(this)

        val restoreImageView: ImageView = findViewById(R.id.image_restore)
        restoreImageView.setOnClickListener(this)
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

                        list.add(data)
                    }
                }
                buildJson(list)
            }
            R.id.image_clear -> {
                photoEditor.clear()
            }
            R.id.image_restore -> {
                val string = readFromFile(this)
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

    private fun buildJson(emojiDataList: List<Emoji>) {
        val gson = Gson()
        val backup = Backup()
        backup.emojis = emojiDataList
        val json = gson.toJson(backup)

        writeToFile(json)
    }
    private fun readFromFile(context: Context): String {
        var ret = ""
        try {
            val inputStream: InputStream? = context.openFileInput("config.txt")
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: " + e.toString())
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: $e")
        }
        return ret
    }
    private fun writeToFile(data: String) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(openFileOutput("config.txt", MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
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

