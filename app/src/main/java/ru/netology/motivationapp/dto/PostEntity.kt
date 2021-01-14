package ru.netology.motivationapp.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val datePublished: String,
    val likes: Int = 0,
    val dislike: Int = 0,
    val pictureName: String,
    val dateCompare: Long
) {

    companion object {
        fun fromPost(post: Post): PostEntity =
            with(post) {
                PostEntity(
                    id,
                    author,
                    content,
                    datePublished,
                    likes,
                    dislike,
                    pictureName,
                    dateCompare
                )
            }
    }
}

fun PostEntity.toPost(): Post =
    Post(
        id,
        author,
        content,
        datePublished,
        likes,
        dislike,
        pictureName,
        dateCompare
    )