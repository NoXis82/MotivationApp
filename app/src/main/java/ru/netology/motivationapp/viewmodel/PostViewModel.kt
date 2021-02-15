package ru.netology.motivationapp.viewmodel

import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.motivationapp.BuildConfig
import ru.netology.motivationapp.R
import ru.netology.motivationapp.db.AppDb
import ru.netology.motivationapp.dto.Post
import ru.netology.motivationapp.repository.*
import java.io.File
import java.io.FileNotFoundException

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val empty = Post(
        id = 0,
        author = "",
        content = "",
        datePublished = "",
        pictureName = "",
        dateCompare = 0
    )
    private val repository: IPostRepository = PostRepositorySQLite(
        AppDb.getInstance(application)
            .postDao()
    )
    val data = repository.getAll()
    private val edited = MutableLiveData(empty)
    fun remove(id: Long) = repository.removePost(id)
    fun like(id: Long) = repository.like(id)
    fun dislike(id: Long) = repository.dislike(id)
    fun savePost() {
        edited.value?.let {
            repository.savePost(it)
        }
        edited.value = empty
    }

    fun changeContent(author: String, content: String, pictureName: String) {
        val contentText = content.trim()
        val authorText = author.trim()
        if (edited.value?.content != contentText) {
            edited.value = edited.value?.copy(content = contentText)
        }
        if (edited.value?.author != authorText) {
            edited.value = edited.value?.copy(author = authorText)
        }
        if (edited.value?.pictureName != pictureName) {
            edited.value = edited.value?.copy(pictureName = pictureName)
        }
    }

    fun editPost(post: Post) {
        edited.value = post
    }

    fun sharePost(post: Post) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.author + "\n" + post.content)
            type = "text/plain"
            if (post.pictureName != "") {
                try {
                    val fileDir = File(
                        getApplication<Application>().filesDir,
                        "images"
                    )
                    fileDir.mkdir()
                    val file = File(fileDir.path, post.pictureName)
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val uriImg = FileProvider.getUriForFile(
                        getApplication<Application>(),
                        BuildConfig.APPLICATION_ID,
                        file
                    )
                    putExtra(Intent.EXTRA_STREAM, uriImg)
                    type = "image/*"
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
        val shareIntent = Intent.createChooser(
            intent,
            R.string.chooser_share_post.toString()
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        getApplication<Application>().startActivity(shareIntent)
    }
}