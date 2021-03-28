package ru.netology.motivationapp.repository

import androidx.lifecycle.LiveData
import ru.netology.motivationapp.dto.*

interface IPostRepository {
    fun getPosts(): LiveData<List<Post>>
    fun like(id: Long)
    fun dislike(id: Long)
    fun savePost(post: Post)
    fun removePost(id: Long)
    fun count(): Long
    fun getRangePosts(startPos: Long, endPos: Long): List<Post>
}