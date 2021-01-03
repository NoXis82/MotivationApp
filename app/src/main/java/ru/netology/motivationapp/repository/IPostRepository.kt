package ru.netology.motivationapp.repository

import androidx.lifecycle.LiveData
import ru.netology.motivationapp.dto.*

interface IPostRepository {
    fun like()
    fun dislike()
    fun getAll(): LiveData<List<Post>>
    fun savePost(post: Post)
    fun editPost()
    fun removePost(id: Long)
    fun share()

}