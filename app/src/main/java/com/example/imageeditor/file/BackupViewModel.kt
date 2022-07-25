package com.example.imageeditor.file

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.imageeditor.core.data.Backup
import com.example.imageeditor.core.data.Emoji
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*

class BackupViewModel(application: Application) : AndroidViewModel(application) {
    private val _status = MutableLiveData<FileAccessStatus>()
    val status: LiveData<FileAccessStatus> = _status

    private val _backup = MutableLiveData<Backup>()
    val backup: LiveData<Backup> = _backup
    private val fileName = "editingObjects.txt"

    fun buildJson(emojiDataList: List<Emoji>) {
        viewModelScope.launch(Dispatchers.IO) {
            _status.postValue(FileAccessStatus.LOADING)
            delay(300)
            val backup = Backup()
            backup.emojis = emojiDataList
            writeToFile(Gson().toJson(backup))
        }
    }

    fun restore() {
        viewModelScope.launch(Dispatchers.IO) {
            _status.postValue(FileAccessStatus.LOADING)
            try {
                val context = getApplication<Application>().applicationContext
                context.openFileInput(fileName)?.let { inputStream ->
                    val inputStreamReader = InputStreamReader(inputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)
                    var receiveString: String? = ""
                    val stringBuilder = StringBuilder()
                    while (bufferedReader.readLine().also { receiveString = it } != null) {
                        stringBuilder.append("\n").append(receiveString)
                    }
                    inputStream.close()
                    _status.postValue(FileAccessStatus.DONE)
                    _backup.postValue(Gson().fromJson(stringBuilder.toString(), Backup::class.java))
                }
            } catch (e: Exception) {
                Log.e(BackupViewModel::class.simpleName, "File read failed:: $e")
                _status.postValue(FileAccessStatus.ERROR)
            }
        }
    }

    private fun writeToFile(data: String) {
        try {
            val context = getApplication<Application>().applicationContext
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(fileName, AppCompatActivity.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
            _status.postValue(FileAccessStatus.DONE)
        } catch (e: IOException) {
            Log.e(BackupViewModel::class.simpleName, "File write failed: $e")
            _status.postValue(FileAccessStatus.ERROR)
        }
    }

}
