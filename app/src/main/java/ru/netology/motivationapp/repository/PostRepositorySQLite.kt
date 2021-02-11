package ru.netology.motivationapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.motivationapp.dao.PostDao
import ru.netology.motivationapp.dto.Post
import ru.netology.motivationapp.dto.PostEntity
import ru.netology.motivationapp.dto.toPost

class PostRepositorySQLite(
        private val dao: PostDao
) : IPostRepository {
    override fun like(id: Long) {
        dao.likeById(id)
    }

    override fun dislike(id: Long) {
        dao.dislikeById(id)
    }

    override fun getAll(): LiveData<List<Post>> = dao.getAll().map { list ->
        list.map {
            it.toPost()
        }
    }

    override fun savePost(post: Post) {
        dao.save(PostEntity.fromPost(post))
    }

    override fun removePost(id: Long) {
        dao.removeById(id)
    }
}