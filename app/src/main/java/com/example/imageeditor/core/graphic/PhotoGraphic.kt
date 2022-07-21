package com.example.imageeditor.core.graphic

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.imageeditor.R
import com.example.imageeditor.core.PhotoEditorViewState
import com.example.imageeditor.core.listener.MultiTouchListener
import com.example.imageeditor.core.view.PhotoEditorView

internal class PhotoGraphic (
    context: Context,
    graphicManager: GraphicManager
) : Graphic(
    context = context,
    graphicManager = graphicManager,
    layoutId = R.layout.view_photo_editor_image
) {
    private lateinit var imageView: ImageView

    init {
        setupView()
    }

    fun buildView(uri: Uri) {
        imageView.setImageURI(uri)
    }

    override fun setupView() {
        imageView = rootView.findViewById(R.id.image_photo_editor)
    }

    override fun rect(): Rect {
        return Rect(0, 0, 0, 0)
    }
}