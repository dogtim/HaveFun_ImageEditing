package com.example.imageeditor.core.graphic

import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.example.imageeditor.R
import com.example.imageeditor.core.PhotoEditorViewState
import com.example.imageeditor.core.listener.MultiTouchListener
import com.example.imageeditor.core.view.PhotoEditorView

internal class PhotoImage(
    private val photoEditorView: PhotoEditorView,
    private val multiTouchListener: MultiTouchListener,
    private val viewState: PhotoEditorViewState,
    graphicManager: GraphicManager?
) : Graphic(
    context = photoEditorView.context,
    graphicManager = graphicManager,
    layoutId = R.layout.view_photo_editor_image
) {
    private var imageView: ImageView? = null

    init {
        setupGesture()
    }

    fun buildView(uri: Uri) {
        imageView?.setImageURI(uri)
    }

    private fun setupGesture() {
        val onGestureControl = buildGestureController(photoEditorView, viewState)
        multiTouchListener.setOnGestureControl(onGestureControl)
        val rootView = rootView
        rootView.setOnTouchListener(multiTouchListener)
    }

    override fun setupView() {
        imageView = rootView.findViewById(R.id.image_photo_editor)
    }

}