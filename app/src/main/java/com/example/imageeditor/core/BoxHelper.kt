package com.example.imageeditor.core

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.imageeditor.R
import com.example.imageeditor.core.view.PhotoEditorView

class BoxHelper(
    private val photoEditorView: PhotoEditorView,
    private val viewState: PhotoEditorViewState
) {

    fun clear() {
        for (i in 0 until photoEditorView.childCount) {
            val childAt = photoEditorView.getChildAt(i)
            childAt.findViewById<FrameLayout>(R.id.editor_border)?.setBackgroundResource(0)
            childAt.findViewById<ImageView>(R.id.image_close)?.visibility = View.GONE
        }
        viewState.clearCurrentSelectedView()
    }

}