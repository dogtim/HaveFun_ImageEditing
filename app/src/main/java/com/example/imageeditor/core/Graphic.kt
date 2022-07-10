package com.example.imageeditor.core

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.example.imageeditor.R
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
        viewState.currentSelectedView
        return object : MultiTouchListener.OnGestureControl {
            override fun onClick() {
                viewState.currentSelectedView = rootView
            }

            override fun onLongClick() {

            }
        }
    }

    open fun setupView(rootView: View) {}
}