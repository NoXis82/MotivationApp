package ru.netology.motivationapp.repository

import androidx.lifecycle.LiveData
import ru.netology.motivationapp.dto.*

interface IPostRepository {
    fun like()
    fun dislike()
    fun getAll(): LiveData<List<Post>>
    fun savePost()
    fun editPost()
    fun removePost()
    fun share()

}