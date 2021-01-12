package ru.netology.motivationapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.motivationapp.dto.Post
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class PostRepositoryInMemoryImpl: IPostRepository {
    private var nextId = 1L
    private var tempValue = 0
    private var posts = listOf(
        Post(
            id = nextId++,
            author = "Нетология",
            content = "Привет 1",
            datePublished = "11/12/2020 15:57",
            pictureName = "",
            dateCompare = nextId
        ),
        Post(
            id = nextId++,
            author = "Нетология",
            content = "Привет 2",
            datePublished = "14/12/2020 15:57",
            pictureName = "23.jpg",
            dateCompare = nextId
        ),
        Post(
            id = nextId++,
            author = "Хабр",
            content = "Привет 3",
            datePublished = "16/12/2020 15:57",
            pictureName = "",
            dateCompare = nextId
        ),
        Post(
            id = nextId++,
            author = "Хабр",
            content = "Привет 4",
            datePublished = "15/12/2020 15:57",
            pictureName = "",
            dateCompare = nextId
        ),
        Post(
            id = nextId++,
            author = "Аноним",
            content = "Привет 5",
            datePublished = "10/12/2020 15:57",
            pictureName = "",
            dateCompare = nextId
        ),
        Post(
            id = nextId++,
            author = "Аноним",
            content = "Привет 6",
            datePublished = "04/01/2021 15:57",
            pictureName = "",
            dateCompare = nextId
        ),
        Post(
            id = nextId++,
            author = "Аноним",
            content = "Привет 7",
            datePublished = "04/01/2021 15:57",
            pictureName = "",
            dateCompare = nextId
        ),
        Post(
            id = nextId++,
            author = "Аноним",
            content = "Привет 8",
            datePublished = "04/01/2021 15:57",
            pictureName = "",
            dateCompare = nextId
        ),
        Post(
            id = nextId++,
            author = "Аноним",
            content = "Привет 9",
            datePublished = "04/01/2021 15:57",
            likes = 23,
            dislike = 45,
            pictureName = "",
            dateCompare = nextId
        ),
        Post(
            id = nextId++,
            author = "Аноним",
            content = "Привет 10",
            datePublished = "04/01/2021 15:57",
            likes = 123,
            dislike = 45,
            pictureName = "",
            dateCompare = nextId
        )

    )


    private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data

    override fun like(id: Long) {
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                tempValue = it.likes
                tempValue++
                it.copy(likes = tempValue)
            }
        }
        data.value = posts
    }

    override fun dislike(id: Long) {
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                tempValue = it.dislike
                tempValue++
                it.copy(dislike = tempValue)
            }
        }
        data.value = posts
    }



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
                   pictureName = post.pictureName,
                   dateCompare = Date().time
                   )
           ) + posts

           data.value = posts
           return
       }
        posts = posts.map {
            if (it.id != post.id) {
                it
            } else {
                it.copy(
                    author = post.author,
                    content = post.content,
                    pictureName = post.pictureName
                )

            }
        }
        data.value = posts
    }

    override fun removePost(id: Long) {
        posts = posts.filter { post ->
            post.id != id
        }
        data.value = posts
    }

}