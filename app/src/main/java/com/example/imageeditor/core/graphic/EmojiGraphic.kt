package com.example.imageeditor.core.graphic

import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.example.imageeditor.R
import com.example.imageeditor.core.data.EmojiData
import com.example.imageeditor.core.listener.MultiTouchListener
import com.example.imageeditor.core.view.PhotoEditorView

internal class EmojiGraphic(
    private val photoEditorView: PhotoEditorView,
    private val multiTouchListener: MultiTouchListener,
    graphicManager: GraphicManager,
    private val data: EmojiData
) : Graphic(
    context = photoEditorView.context,
    graphicManager = graphicManager,
    layoutId = R.layout.view_photo_editor_text
) {
    private lateinit var txtEmoji: TextView

    init {
        setupGesture()
        setupView()
    }

    private fun setupGesture() {
        val onGestureControl = buildGestureController(graphicManager.viewState)
        multiTouchListener.setOnGestureControl(onGestureControl)
        val rootView = rootView
        rootView.setOnTouchListener(multiTouchListener)
    }

    override fun setupView() {
        txtEmoji = rootView.findViewById(R.id.text_photo_editor)
        txtEmoji.run {
            gravity = Gravity.CENTER
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }

        txtEmoji.apply {
            if (data.typeface != null) {
                typeface = data.typeface
            }
            textSize = 56f
            text = data.name
        }
    }

    override fun rect(): Rect {
        return Rect(data.left, data.top, data.left, data.top)
    }

}