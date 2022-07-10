package com.example.imageeditor.core

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.imageeditor.core.listener.MultiTouchListener
import com.example.imageeditor.core.view.PhotoEditorView

internal abstract class Graphic(
    val context: Context,
    val layoutId: Int) {

    val rootView: View = LayoutInflater.from(context).inflate(layoutId, null)

    init {
        setupView(rootView)
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