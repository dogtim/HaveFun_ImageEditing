package com.example.imageeditor.core

import android.view.View
import java.util.*
/**
 * The View State which contains how much addedView and which the selected view it is
 * We can operate the undo/redo according to this information
 *
 */
class PhotoEditorViewState {
    var selectedView: View? = null
    private val addedViews: MutableList<View>
    private val redoViews: Stack<View>

    init {
        addedViews = ArrayList()
        redoViews = Stack()
    }

    fun clearCurrentSelectedView() {
        selectedView = null
    }

    fun getAddedView(index: Int): View {
        return addedViews[index]
    }

    val addedViewsCount: Int
        get() = addedViews.size

    fun addAddedView(view: View) {
        addedViews.add(view)
    }

    fun removeAddedView(index: Int): View {
        return addedViews.removeAt(index)
    }

    fun pushRedoView(view: View) {
        redoViews.push(view)
    }

    fun popRedoView(): View {
        return redoViews.pop()
    }

    val redoViewsCount: Int
        get() = redoViews.size

    fun getRedoView(index: Int): View {
        return redoViews[index]
    }

}