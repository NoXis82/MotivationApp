package ru.netology.motivationapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.motivationapp.dto.Post

class PostRepositoryInMemoryImpl: IPostRepository {
    private val posts = listOf(
        Post(
            id = 1,
            author = "Нетология",
            content = "Привет 1",
            datePublished = "11/12/2020 15:57",
            videoUrl = "",
        ),
        Post(
            id = 2,
            author = "Нетология",
            content = "Привет 2",
            datePublished = "14/12/2020 15:57",
            videoUrl = "",
        )
    )
    private val data = MutableLiveData(posts)

    override fun like() {
        TODO("Not yet implemented")
    }

    override fun dislike() {
        TODO("Not yet implemented")
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun savePost() {
        TODO("Not yet implemented")
    }

    override fun editPost() {
        TODO("Not yet implemented")
    }

    override fun removePost() {
        TODO("Not yet implemented")
    }

    override fun share() {
        TODO("Not yet implemented")
    }


}