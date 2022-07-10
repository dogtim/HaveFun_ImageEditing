package com.example.imageeditor.core.scale

import android.view.View
import com.example.imageeditor.core.listener.MultiTouchListener
import com.example.imageeditor.core.listener.ScaleGestureDetector
import kotlin.math.max
import kotlin.math.min

class ScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
    private val isTranslateEnabled = true
    private var mPivotX = 0f
    private var mPivotY = 0f
    private val minimumScale = 0.5f
    private val maximumScale = 10.0f
    private val isRotateEnabled = true
    private val isScaleEnabled = true
    private val mIsPinchScalable = true
    private val mPrevSpanVector = Vector2D()

    override fun onScaleBegin(view: View, detector: ScaleGestureDetector): Boolean {
        mPivotX = detector.getFocusX()
        mPivotY = detector.getFocusY()
        mPrevSpanVector.set(detector.getCurrentSpanVector())
        return mIsPinchScalable
    }

    override fun onScale(view: View, detector: ScaleGestureDetector): Boolean {
        val info = TransformInfo()
        info.deltaScale = if (isScaleEnabled) detector.getScaleFactor() else 1.0f
        info.deltaAngle = if (isRotateEnabled) Vector2D.getAngle(
            mPrevSpanVector,
            detector.getCurrentSpanVector()
        ) else 0.0f
        info.deltaX = if (isTranslateEnabled) detector.getFocusX() - mPivotX else 0.0f
        info.deltaY = if (isTranslateEnabled) detector.getFocusY() - mPivotY else 0.0f
        info.pivotX = mPivotX
        info.pivotY = mPivotY
        info.minimumScale = minimumScale
        info.maximumScale = maximumScale
        move(view, info)
        return !mIsPinchScalable
    }

    private fun adjustAngle(degrees: Float): Float {
        return when {
            degrees > 180.0f -> {
                degrees - 360.0f
            }
            degrees < -180.0f -> {
                degrees + 360.0f
            }
            else -> degrees
        }
    }

    private fun move(view: View, info: TransformInfo) {
        computeRenderOffset(view, info.pivotX, info.pivotY)
        adjustTranslation(view, info.deltaX, info.deltaY)
        var scale = view.scaleX * info.deltaScale
        scale = max(info.minimumScale, min(info.maximumScale, scale))
        view.scaleX = scale
        view.scaleY = scale
        val rotation = adjustAngle(view.rotation + info.deltaAngle)
        view.rotation = rotation
    }

    private fun adjustTranslation(view: View, deltaX: Float, deltaY: Float) {
        val deltaVector = floatArrayOf(deltaX, deltaY)
        view.matrix.mapVectors(deltaVector)
        view.translationX = view.translationX + deltaVector[0]
        view.translationY = view.translationY + deltaVector[1]
    }

    private fun computeRenderOffset(view: View, pivotX: Float, pivotY: Float) {
        if (view.pivotX == pivotX && view.pivotY == pivotY) {
            return
        }
        val prevPoint = floatArrayOf(0.0f, 0.0f)
        view.matrix.mapPoints(prevPoint)
        view.pivotX = pivotX
        view.pivotY = pivotY
        val currPoint = floatArrayOf(0.0f, 0.0f)
        view.matrix.mapPoints(currPoint)
        val offsetX = currPoint[0] - prevPoint[0]
        val offsetY = currPoint[1] - prevPoint[1]
        view.translationX = view.translationX - offsetX
        view.translationY = view.translationY - offsetY
    }
}