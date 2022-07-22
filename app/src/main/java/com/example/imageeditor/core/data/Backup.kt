package com.example.imageeditor.core.data

import com.google.gson.annotations.SerializedName

class Backup {
    @SerializedName("emojis")
    var emojis: List<Emoji>? = null
}