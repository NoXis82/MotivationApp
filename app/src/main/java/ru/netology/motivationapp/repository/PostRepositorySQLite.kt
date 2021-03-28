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

    override fun getPosts(): LiveData<List<Post>> =
        dao.getPosts().map { it.map(PostEntity::toPost) }

    override fun like(id: Long) {
        dao.likeById(id)
    }

    override fun dislike(id: Long) {
        dao.dislikeById(id)
    }

    override fun savePost(post: Post) {
        dao.save(PostEntity.fromPost(post))
    }

    override fun removePost(id: Long) {
        dao.removeById(id)
    }

    override fun count(): Long = dao.count()

    override fun getRangePosts(startPos: Long, endPos: Long): List<Post> =
        dao.getRangePosts(startPos, endPos).map(PostEntity::toPost)
}