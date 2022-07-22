package com.example.imageeditor.core.data

import android.graphics.Typeface
import com.google.gson.annotations.SerializedName

data class Emoji(@SerializedName("name") val name: String) {
    @SerializedName("top") var top: Int = 0
    @SerializedName("left") var left: Int = 0
    @SerializedName("rotation") var rotation: Float = 0f
    @SerializedName("typeface") var typeface: Typeface? = null
}