package ru.netology.motivationapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.motivationapp.dto.Post
import ru.netology.motivationapp.repository.*


class PostViewModel: ViewModel() {

    private val empty = Post(
        id = 0,
        author = "",
        content = "",
        datePublished = "",
        pictureName = "",
        dateCompare = 0
    )

    private val repository: IPostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    private val edited = MutableLiveData(empty)
    fun remove(id: Long) = repository.removePost(id)
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

    fun editPost(post: Post) {
        edited.value = post
    }

}