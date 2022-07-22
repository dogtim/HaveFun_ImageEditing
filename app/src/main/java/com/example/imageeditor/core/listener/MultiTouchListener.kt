package com.example.imageeditor.core.listener

import android.graphics.Rect
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.example.imageeditor.core.PhotoEditorViewState
import com.example.imageeditor.core.scale.ScaleGestureListener

// https://www.twblogs.net/a/5cd35c7abd9eee6726c953a8
class MultiTouchListener(
    private val viewState: PhotoEditorViewState,
    private val gestureDetector: GestureDetector
) : OnTouchListener {
    // To handle the rotate & scale operation
    private val scaleGestureDetector = ScaleGestureDetector(ScaleGestureListener())
    private val isTranslateEnabled = true

    private var activePointerId = INVALID_POINTER_ID
    private var prevX = 0f
    private var prevY = 0f
    private var prevRawX = 0f
    private var prevRawY = 0f
    private var outRect: Rect? = null

    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    init {
        outRect = Rect(0, 0, 0, 0)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(view, event)
        gestureDetector.onTouchEvent(event)
        if (!isTranslateEnabled) {
            return true
        }
        val action = event.action
        when (action and event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                prevX = event.x
                prevY = event.y
                prevRawX = event.rawX
                prevRawY = event.rawY
                activePointerId = event.getPointerId(0)
                view.bringToFront()
            }
            MotionEvent.ACTION_MOVE ->
                // Only enable dragging on focused stickers.
                if (view === viewState.selectedView) {
                    val pointerIndexMove = event.findPointerIndex(activePointerId)
                    if (pointerIndexMove != -1) {
                        val currX = event.getX(pointerIndexMove)
                        val currY = event.getY(pointerIndexMove)
                        if (!scaleGestureDetector.isInProgress) {
                            adjustTranslation(view, currX - prevX, currY - prevY)
                        }
                    }
                }
            MotionEvent.ACTION_CANCEL -> activePointerId = INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndexPointerUp =
                    action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = event.getPointerId(pointerIndexPointerUp)
                if (pointerId == activePointerId) {
                    val newPointerIndex = if (pointerIndexPointerUp == 0) 1 else 0
                    prevX = event.getX(newPointerIndex)
                    prevY = event.getY(newPointerIndex)
                    activePointerId = event.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }

    interface OnGestureControl {
        fun onClick()
    }

    private fun adjustTranslation(view: View, deltaX: Float, deltaY: Float) {
        val deltaVector = floatArrayOf(deltaX, deltaY)
        view.matrix.mapVectors(deltaVector)
        view.translationX = view.translationX + deltaVector[0]
        view.translationY = view.translationY + deltaVector[1]
    }

}