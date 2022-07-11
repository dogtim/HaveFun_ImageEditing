package com.example.imageeditor.core.listener

import android.graphics.Rect
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.example.imageeditor.core.PhotoEditorViewState
import com.example.imageeditor.core.scale.ScaleGestureListener
import com.example.imageeditor.core.view.PhotoEditorView

internal class MultiTouchListener(
    private val photoEditorView: PhotoEditorView,
    private val viewState: PhotoEditorViewState
) : OnTouchListener {
    // To handle the rotate & scale operation
    private val scaleGestureDetector = ScaleGestureDetector(ScaleGestureListener())
    private val gestureListener: GestureDetector
    private val isTranslateEnabled = true

    private var mActivePointerId = INVALID_POINTER_ID
    private var mPrevX = 0f
    private var mPrevY = 0f
    private var mPrevRawX = 0f
    private var mPrevRawY = 0f
    private var outRect: Rect? = null
    private var mOnGestureControl: OnGestureControl? = null

    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    init {
        gestureListener = GestureDetector(this.photoEditorView.context, GestureListener())
        outRect = Rect(0, 0, 0, 0)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(view, event)
        gestureListener.onTouchEvent(event)
        if (!isTranslateEnabled) {
            return true
        }
        val action = event.action
        when (action and event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mPrevX = event.x
                mPrevY = event.y
                mPrevRawX = event.rawX
                mPrevRawY = event.rawY
                mActivePointerId = event.getPointerId(0)
                view.bringToFront()
            }
            MotionEvent.ACTION_MOVE ->
                // Only enable dragging on focused stickers.
                if (view === viewState.currentSelectedView) {
                    val pointerIndexMove = event.findPointerIndex(mActivePointerId)
                    if (pointerIndexMove != -1) {
                        val currX = event.getX(pointerIndexMove)
                        val currY = event.getY(pointerIndexMove)
                        if (!scaleGestureDetector.isInProgress) {
                            adjustTranslation(view, currX - mPrevX, currY - mPrevY)
                        }
                    }
                }
            MotionEvent.ACTION_CANCEL -> mActivePointerId = INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndexPointerUp =
                    action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = event.getPointerId(pointerIndexPointerUp)
                if (pointerId == mActivePointerId) {
                    val newPointerIndex = if (pointerIndexPointerUp == 0) 1 else 0
                    mPrevX = event.getX(newPointerIndex)
                    mPrevY = event.getY(newPointerIndex)
                    mActivePointerId = event.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }

    internal interface OnGestureControl {
        fun onClick()
    }

    fun setOnGestureControl(onGestureControl: OnGestureControl?) {
        mOnGestureControl = onGestureControl
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            mOnGestureControl?.onClick()
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
        }
    }

    private fun adjustTranslation(view: View, deltaX: Float, deltaY: Float) {
        val deltaVector = floatArrayOf(deltaX, deltaY)
        view.matrix.mapVectors(deltaVector)
        view.translationX = view.translationX + deltaVector[0]
        view.translationY = view.translationY + deltaVector[1]
    }

}