package com.example.imageeditor.core.graphic

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
    private val photoEditorView: PhotoEditorView,
    private val multiTouchListener: MultiTouchListener,
    graphicManager: GraphicManager
) : Graphic(
    context = photoEditorView.context,
    graphicManager = graphicManager,
    layoutId = R.layout.view_photo_editor_image
) {
    private lateinit var imageView: ImageView

    init {
        setupGesture()
        setupView()
    }

    fun buildView(uri: Uri) {
        imageView.setImageURI(uri)
    }

    private fun setupGesture() {
        val onGestureControl = buildGestureController(graphicManager.viewState)
        multiTouchListener.setOnGestureControl(onGestureControl)
        val rootView = rootView
        rootView.setOnTouchListener(multiTouchListener)
    }

    override fun setupView() {
        imageView = rootView.findViewById(R.id.image_photo_editor)
    }

    override fun rect(): Rect {
        return Rect(0, 0, 0, 0)
    }
}