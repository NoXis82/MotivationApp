package ru.netology.motivationapp.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.motivationapp.App
import ru.netology.motivationapp.R
import ru.netology.motivationapp.dto.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CreatePostViewModel(application: Application) : AndroidViewModel(application) {
    private val empty = Post(
        id = 0,
        author = "",
        content = "",
        datePublished = "",
        pictureName = "",
        dateCompare = 0
    )
    private val repository = App.repositorySQLite
    private val edited = MutableLiveData(empty)
    fun savePost() {
        edited.value?.let {
            repository.savePost(it)
        }
        edited.value = empty
    }

    fun loadImage(pictureName: String): Bitmap {
        val fileDir = File(getApplication<Application>().applicationContext.filesDir, "images")
        fileDir.mkdir()
        val file = File(fileDir, pictureName)
        return BitmapFactory.decodeFile(file.toString())
    }

    fun saveImageToExternal(bitmap: Bitmap, filename: String) {
        val fileDir = File(getApplication<Application>().applicationContext.filesDir, "images")
        fileDir.mkdir()
        val file = File(fileDir, filename)
        if (file.exists()) {
            file.delete()
        }
        try {
            val streamOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, streamOut)
            streamOut.flush()
            streamOut.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(
                getApplication<Application>().applicationContext,
                getApplication<Application>().applicationContext
                    .getString(R.string.error_save_image),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    fun changeContent(author: String, content: String, pictureName: String) {
        val contentText = content.trim()
        val authorText = author.trim()
        if (edited.value?.content != contentText) {
            edited.value = edited.value?.copy(content = contentText)
        }
        if (edited.value?.author != authorText) {
            edited.value = edited.value?.copy(author = authorText)
        }
        if (edited.value?.pictureName != pictureName) {
            edited.value = edited.value?.copy(pictureName = pictureName)
        }
    }
}