package com.example.imageeditor.core.graphic

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.example.imageeditor.R
import com.example.imageeditor.core.PhotoEditorViewState
import com.example.imageeditor.core.view.PhotoEditorView

class GraphicManager(private val photoEditorView: PhotoEditorView) {
    val viewState: PhotoEditorViewState = PhotoEditorViewState()

    /**
     * Adds a [graphic] to this [photoEditorView], and keep the view state in [viewState]
     * The adding View will set to the central position in its parent's View
     */
    fun addView(graphic: Graphic) {
        val view = graphic.rootView
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (graphic.attributes.left != 0 || graphic.attributes.top != 0) {
            params.marginStart = graphic.attributes.left
            params.topMargin = graphic.attributes.top
            view.rotation = graphic.attributes.rotation
            view.scaleX = graphic.attributes.scaleX
            view.scaleY = graphic.attributes.scaleY
        } else {
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        }

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

    fun clearAppearance() {
        for (i in 0 until photoEditorView.childCount) {
            val childAt = photoEditorView.getChildAt(i)
            childAt.findViewById<FrameLayout>(R.id.editor_border)?.setBackgroundResource(0)
            childAt.findViewById<ImageView>(R.id.image_close)?.visibility = View.GONE
        }
        viewState.clearCurrentSelectedView()
    }

    fun clear() {
        photoEditorView.removeAllViews()
        viewState.clear()
    }
}