package ru.netology.motivationapp.dto

data class Post(
    var id: Long,
    val author: String,
    val content: String,
    val datePublished: String,
    val likes: Int = 0,
    val share: Int = 0,
    val videoUrl: String
)
