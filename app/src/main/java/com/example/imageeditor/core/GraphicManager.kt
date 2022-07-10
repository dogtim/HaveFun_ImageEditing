package com.example.imageeditor.core

import android.view.ViewGroup
import android.widget.RelativeLayout
import com.example.imageeditor.core.view.PhotoEditorView

internal class GraphicManager(
    private val photoEditorView: PhotoEditorView,
    private val viewState: PhotoEditorViewState
) {

    /**
     * Adds a [graphic] to this [photoEditorView], and keep the view state in [viewState]
     * The adding View will set to the central position in its parent's View
     */
    fun addView(graphic: Graphic) {
        val view = graphic.rootView
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        photoEditorView.addView(view, params)
        viewState.addAddedView(view)
    }

    fun removeView(graphic: Graphic) {
        val view = graphic.rootView
        photoEditorView.removeView(view)
    }

    fun undoView(): Boolean {
        if (viewState.addedViewsCount > 0) {
            val removeView = viewState.getAddedView(
                viewState.addedViewsCount - 1
            )
            viewState.removeAddedView(viewState.addedViewsCount - 1)
            photoEditorView.removeView(removeView)
            viewState.pushRedoView(removeView)
        }
        return viewState.addedViewsCount != 0
    }

    fun redoView(): Boolean {
        if (viewState.redoViewsCount > 0) {
            val redoView = viewState.getRedoView(
                viewState.redoViewsCount - 1
            )

            viewState.popRedoView()
            photoEditorView.addView(redoView)
            viewState.addAddedView(redoView)

        }
        return viewState.redoViewsCount != 0
    }
}