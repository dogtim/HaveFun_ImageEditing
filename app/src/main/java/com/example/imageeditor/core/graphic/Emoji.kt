package com.example.imageeditor.core.graphic

import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.example.imageeditor.R
import com.example.imageeditor.core.PhotoEditorViewState
import com.example.imageeditor.core.listener.MultiTouchListener
import com.example.imageeditor.core.view.PhotoEditorView

internal class Emoji(
    private val photoEditorView: PhotoEditorView,
    private val viewState: PhotoEditorViewState,
    private val multiTouchListener: MultiTouchListener,
    graphicManager: GraphicManager?,
) : Graphic(
    context = photoEditorView.context,
    graphicManager = graphicManager,
    layoutId = R.layout.view_photo_editor_text
) {
    private var txtEmoji: TextView? = null

    init {
        setupGesture()
    }

    private fun setupGesture() {
        val onGestureControl = buildGestureController(photoEditorView, viewState)
        multiTouchListener.setOnGestureControl(onGestureControl)
        val rootView = rootView
        rootView.setOnTouchListener(multiTouchListener)
    }

    fun buildView(emojiTypeface: Typeface?, emojiName: String?) {
        txtEmoji?.apply {
            if (emojiTypeface != null) {
                typeface = emojiTypeface
            }
            textSize = 56f
            text = emojiName
        }
    }

    override fun setupView() {
        txtEmoji = rootView.findViewById(R.id.text_photo_editor)
        txtEmoji?.run {
            gravity = Gravity.CENTER
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }

}