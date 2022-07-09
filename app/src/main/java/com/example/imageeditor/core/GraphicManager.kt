package com.example.imageeditor.core

import android.view.ViewGroup
import android.widget.RelativeLayout
import com.example.imageeditor.core.view.PhotoEditorView

internal class GraphicManager(
    private val photoEditorView: PhotoEditorView
) {
    fun addView(graphic: Graphic) {
        val view = graphic.rootView
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        photoEditorView.addView(view, params)
    }

}