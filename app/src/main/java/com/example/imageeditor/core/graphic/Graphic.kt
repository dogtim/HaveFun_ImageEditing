package com.example.imageeditor.core.graphic

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.example.imageeditor.R
import com.example.imageeditor.core.listener.MultiTouchListener

abstract class Graphic(
    val context: Context,
    val layoutId: Int,
    val graphicManager: GraphicManager) {

    val rootView: View = LayoutInflater.from(context).inflate(layoutId, null)
    abstract fun setupView()
    abstract fun rect(): Rect

    init {
        setupGesture()
        setupRemoveView()
    }

    private fun setupGesture() {
        val onGestureControl = buildGestureController()
        val multiTouchListener = getMultiTouchListener()
        multiTouchListener.setOnGestureControl(onGestureControl)
        rootView.setOnTouchListener(multiTouchListener)
    }

    private fun setupRemoveView() {
        rootView.findViewById<ImageView>(R.id.image_close)?.setOnClickListener {
            graphicManager.removeView(this@Graphic)
        }
    }

    private fun buildGestureController(): MultiTouchListener.OnGestureControl {
        return object : MultiTouchListener.OnGestureControl {
            /**
             * 1. Set GONE to the Border which is visibility
             * 2. Set Visibility = TRUE to the Border which user select
             */
            override fun onClick() {
                graphicManager.clearAppearance()
                toggleSelection()
                graphicManager.viewState.selectedView = rootView
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

    private fun getMultiTouchListener(): MultiTouchListener {
        return MultiTouchListener(
            context,
            graphicManager.viewState
        )
    }

}