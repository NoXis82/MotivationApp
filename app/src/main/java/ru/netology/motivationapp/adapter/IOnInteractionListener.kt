package ru.netology.motivationapp.adapter

import ru.netology.motivationapp.dto.*

interface IOnInteractionListener {
    fun onLike(post: Post)
    fun onDisLike(post: Post)
    fun onShare(post: Post)

}