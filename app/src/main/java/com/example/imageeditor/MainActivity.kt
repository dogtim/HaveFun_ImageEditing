package com.example.imageeditor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.imageeditor.core.PhotoEditor
import com.example.imageeditor.core.view.PhotoEditorView
import com.example.imageeditor.fragment.ShapeFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MainActivity : AppCompatActivity(), EditorAdapter.OnEditorSelectedListener,
    ShapeFragment.ShapeListener {
    private lateinit var shapeFragment: ShapeFragment
    var photoEditor: PhotoEditor? = null
    private var photoEditorView: PhotoEditorView? = null

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

        initRecycleView()
    }

    private fun initRecycleView() {
        val recyclerView: RecyclerView = findViewById(R.id.recycler_editing_tools)
        recyclerView.adapter = EditorAdapter(this)
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
}