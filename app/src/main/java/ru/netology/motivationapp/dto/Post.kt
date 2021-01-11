package ru.netology.motivationapp.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val datePublished: String,
    val likes: Int = 0,
    val dislike: Int = 0,
    val pictureName: String,
    val dateCompare: Long
)
