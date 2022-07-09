package com.example.imageeditor.core

import android.content.Context
import android.view.LayoutInflater
import android.view.View

internal abstract class Graphic(
    val context: Context,
    val layoutId: Int) {

    val rootView: View = LayoutInflater.from(context).inflate(layoutId, null)

    init {
        setupView(rootView)
    }

    open fun setupView(rootView: View) {}
}