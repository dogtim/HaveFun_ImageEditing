package com.example.imageeditor.core.graphic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.example.imageeditor.R
import com.example.imageeditor.core.BoxHelper
import com.example.imageeditor.core.PhotoEditorViewState
import com.example.imageeditor.core.listener.MultiTouchListener
import com.example.imageeditor.core.view.PhotoEditorView

internal abstract class Graphic(
    val context: Context,
    val layoutId: Int,
    // TODO We should decouple the manager into Graphic, instead it of callback
    val graphicManager: GraphicManager?) {

    val rootView: View = LayoutInflater.from(context).inflate(layoutId, null)

    init {
        setupView(rootView)
        setupRemoveView(rootView)
    }

    private fun setupRemoveView(rootView: View) {
        rootView.findViewById<ImageView>(R.id.image_close)?.setOnClickListener {
            graphicManager?.removeView(this@Graphic)
        }
    }

    protected fun buildGestureController(
        photoEditorView: PhotoEditorView,
        viewState: PhotoEditorViewState
    ): MultiTouchListener.OnGestureControl {
        val boxHelper = BoxHelper(photoEditorView, viewState)
        return object : MultiTouchListener.OnGestureControl {
            /**
             * 1. Set GONE to the Border which is visibility
             * 2. Set Visibility = TRUE to the Border which user select
             */
            override fun onClick() {
                boxHelper.clear()
                toggleSelection()
                viewState.currentSelectedView = rootView
            }

            override fun onLongClick() {

            }
        }
    }

    protected fun toggleSelection() {
        rootView.findViewById<View>(R.id.editor_border)?.let {
            it.setBackgroundResource(R.drawable.rounded_border_tv)
            it.tag = true
        }
        rootView.findViewById<View>(R.id.image_close)?.let {
            it.visibility = View.VISIBLE
        }
    }

    open fun setupView(rootView: View) {}
}