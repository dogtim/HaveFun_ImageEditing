package com.example.imageeditor.core.graphic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.example.imageeditor.R
import com.example.imageeditor.core.PhotoEditorViewState
import com.example.imageeditor.core.listener.MultiTouchListener

abstract class Graphic(
    val context: Context,
    val layoutId: Int,
    val graphicManager: GraphicManager) {

    val rootView: View = LayoutInflater.from(context).inflate(layoutId, null)

    init {
        setupView()
        setupRemoveView()
    }

    private fun setupRemoveView() {
        rootView.findViewById<ImageView>(R.id.image_close)?.setOnClickListener {
            graphicManager.removeView(this@Graphic)
        }
    }

    protected fun buildGestureController(
        viewState: PhotoEditorViewState
    ): MultiTouchListener.OnGestureControl {
        return object : MultiTouchListener.OnGestureControl {
            /**
             * 1. Set GONE to the Border which is visibility
             * 2. Set Visibility = TRUE to the Border which user select
             */
            override fun onClick() {
                graphicManager.clear()
                toggleSelection()
                viewState.currentSelectedView = rootView
            }

        }
    }

    private fun toggleSelection() {
        rootView.findViewById<View>(R.id.editor_border)?.let {
            it.setBackgroundResource(R.drawable.rounded_border_tv)
            it.tag = true
        }
        rootView.findViewById<View>(R.id.image_close)?.let {
            it.visibility = View.VISIBLE
        }
    }

    open fun setupView() {}
}