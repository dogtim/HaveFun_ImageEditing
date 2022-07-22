package com.example.imageeditor.core.graphic

import android.content.Context
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.example.imageeditor.R
import com.example.imageeditor.core.data.Emoji

internal class EmojiGraphic(
    context: Context,
    graphicManager: GraphicManager,
    private val data: Emoji
) : Graphic(
    context = context,
    graphicManager = graphicManager,
    layoutId = R.layout.view_photo_editor_text
) {
    private lateinit var txtEmoji: TextView
    override var attributes: GraphicAttributes = GraphicAttributes(data.left, data.top, data.rotation)

    init {
        setupView()
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

}