package ru.netology.motivationapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.motivationapp.dto.Post
import java.text.SimpleDateFormat
import java.util.*

class PostRepositoryInMemoryImpl: IPostRepository {
    private var nextId = 1L
    private var posts = listOf(
        Post(
            id = nextId++,
            author = "Нетология",
            content = "Привет 1",
            datePublished = "11/12/2020 15:57",
            pictureName = "",
        ),
        Post(
            id = nextId++,
            author = "Нетология",
            content = "Привет 2",
            datePublished = "14/12/2020 15:57",
            pictureName = "23.jpg",
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

    override fun savePost(post: Post) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
        val currentDate = dateFormat.format(Date())

       if (post.id == 0L) {
           posts = listOf(
               post.copy(
                   id = nextId++,
                    author = post.author,
                   content = post.content,
                   datePublished = currentDate,
                   pictureName = post.pictureName)
           ) + posts
           data.value = posts
           return
       }
        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
    }

    override fun editPost() {
        TODO("Not yet implemented")
    }


    override fun removePost(id: Long) {
        posts = posts.filter { post ->
            post.id != id
        }
        data.value = posts
    }

    override fun share() {
        TODO("Not yet implemented")
    }


}