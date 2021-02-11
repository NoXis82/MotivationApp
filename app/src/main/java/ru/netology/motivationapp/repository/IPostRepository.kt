package ru.netology.motivationapp.repository

import androidx.lifecycle.LiveData
import ru.netology.motivationapp.dto.*

interface IPostRepository {
    fun like(id: Long)
    fun dislike(id: Long)
    fun getAll(): LiveData<List<Post>>
    fun savePost(post: Post)
    fun removePost(id: Long)
}