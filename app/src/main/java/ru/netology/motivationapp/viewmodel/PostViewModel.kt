package ru.netology.motivationapp.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.motivationapp.dto.Post
import ru.netology.motivationapp.repository.*


class PostViewModel: ViewModel() {
    private val repository: IPostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    fun remove(id: Long) = repository.removePost(id)

    fun editPost(post: Post) {

    }

}