package com.example.imageeditor.file

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.imageeditor.core.data.Backup
import com.example.imageeditor.core.data.Emoji
import com.google.gson.Gson
import java.io.*

class BackupViewModel() : ViewModel() {
    private val _status = MutableLiveData<FileAccessStatus>()
    val status: LiveData<FileAccessStatus> = _status

    fun buildJson(emojiDataList: List<Emoji>, context: Context) {
        val gson = Gson()
        val backup = Backup()
        backup.emojis = emojiDataList
        val json = gson.toJson(backup)

        writeToFile(json, context)
    }

    fun readFromFile(context: Context): String {
        var ret = ""
        try {
            val inputStream: InputStream? = context.openFileInput("config.txt")
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: " + e.toString())
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: $e")
        }
        return ret
    }

    fun writeToFile(data: String, context: Context) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput("config.txt", AppCompatActivity.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }

}
