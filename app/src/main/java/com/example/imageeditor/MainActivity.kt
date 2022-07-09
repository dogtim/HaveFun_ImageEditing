package com.example.imageeditor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.imageeditor.fragment.ShapeFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MainActivity : AppCompatActivity(), EditorAdapter.OnEditorSelectedListener {
    private lateinit var shapeFragment: ShapeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shapeFragment = ShapeFragment()
        setContentView(R.layout.activity_main)
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
}