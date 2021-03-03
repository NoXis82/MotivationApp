package ru.netology.motivationapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.motivationapp.App
import ru.netology.motivationapp.dto.*

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