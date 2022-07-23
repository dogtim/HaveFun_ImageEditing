package com.example.imageeditor.file

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.imageeditor.core.data.Backup
import com.example.imageeditor.core.data.Emoji
import com.google.gson.Gson
import java.io.*

class BackupViewModel : ViewModel() {
    private val _status = MutableLiveData<FileAccessStatus>()
    val status: LiveData<FileAccessStatus> = _status
    val fileName = "editingObjects.txt"
    fun buildJson(emojiDataList: List<Emoji>, context: Context) {
        _status.value = FileAccessStatus.LOADING
        val gson = Gson()
        val backup = Backup()
        backup.emojis = emojiDataList
        val json = gson.toJson(backup)

        writeToFile(json, context)
    }

    fun readFromFile(context: Context): String {
        _status.value = FileAccessStatus.LOADING
        var ret = ""
        try {
            context.openFileInput(fileName)?.let { inputStream ->
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                ret = stringBuilder.toString()
                _status.value = FileAccessStatus.DONE
            }
        } catch (e: FileNotFoundException) {
            Log.e(BackupViewModel::class.simpleName, "File not found: $e")
            _status.value = FileAccessStatus.ERROR
        } catch (e: IOException) {
            Log.e(BackupViewModel::class.simpleName, "Can not read file: $e")
            _status.value = FileAccessStatus.ERROR
        }
        return ret
    }

    private fun writeToFile(data: String, context: Context) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(fileName, AppCompatActivity.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
            _status.value = FileAccessStatus.DONE
        } catch (e: IOException) {
            Log.e(BackupViewModel::class.simpleName, "File write failed: $e")
            _status.value = FileAccessStatus.ERROR
        }
    }

}
