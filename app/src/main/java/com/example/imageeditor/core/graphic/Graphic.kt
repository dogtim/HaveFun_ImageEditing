package com.example.imageeditor.core.graphic

import android.content.Context
import android.graphics.Rect
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.example.imageeditor.R
import com.example.imageeditor.core.listener.MultiTouchListener

data class GraphicAttributes(
    var left: Int = 0,
    var top: Int  = 0,
    var rotation: Float = 0f
)

abstract class Graphic(
    val context: Context,
    val layoutId: Int,
    val graphicManager: GraphicManager) {

    val rootView: View = LayoutInflater.from(context).inflate(layoutId, null)
    abstract fun setupView()
    abstract var attributes: GraphicAttributes

    init {
        rootView.setOnTouchListener(getMultiTouchListener())
        rootView.findViewById<ImageView>(R.id.image_close)?.setOnClickListener {
            graphicManager.removeView(this@Graphic)
        }
    }

    private fun getMultiTouchListener(): MultiTouchListener {
        return MultiTouchListener(
            graphicManager.viewState,
            GestureDetector(context, GestureListener())
        )
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        /**
         * 1. Set GONE to the Border which is visibility
         * 2. Set Visibility = TRUE to the Border which user select
         */
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            graphicManager.clearAppearance()
            toggleSelection()
            graphicManager.viewState.selectedView = rootView
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
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
    }
}