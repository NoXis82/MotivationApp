package ru.netology.motivationapp.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.motivationapp.repository.*


class PostViewModel: ViewModel() {
    private val repository: IPostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()

}