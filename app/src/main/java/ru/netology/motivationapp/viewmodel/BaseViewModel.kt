package ru.netology.motivationapp.viewmodel

import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import ru.netology.motivationapp.App
import ru.netology.motivationapp.BuildConfig
import ru.netology.motivationapp.R
import ru.netology.motivationapp.dto.Post
import ru.netology.motivationapp.model.FeedModel
import java.io.File
import java.io.FileNotFoundException

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val STEP_LIMIT = 20L
    }

    private var startPos = 0L
    private var endPos = STEP_LIMIT
    private val empty = Post(
        id = 0,
        author = "",
        content = "",
        datePublished = "",
        pictureName = "",
        dateCompare = 0
    )
    private val repository = App.repositorySQLite
    private var cash = emptyList<Post>()
    private val totalItems = repository.count()
    private val edited = MutableLiveData(empty)
    private val _state = MutableLiveData(FeedModel())
    val state: LiveData<FeedModel>
        get() = _state
    private var _data = MutableLiveData<List<Post>>()
    val data: LiveData<List<Post>>
        get() = _data

    fun remove(id: Long) = repository.removePost(id)
    fun like(id: Long) = repository.like(id)
    fun dislike(id: Long) = repository.dislike(id)
    fun editPost(post: Post) {
        edited.value = post
    }

    init {
        load()
    }

    private fun load() {
        _state.value = FeedModel(loading = true)
        _data.value = repository.getRangePosts(startPos, endPos)
        _state.value = FeedModel()
    }

    fun sortedList(posts: List<Post>): List<Post> = posts
        .sortedWith(compareBy { it.dateCompare })
        .sortedWith { post1, post2 ->
            (post2.likes - post2.dislike) - (post1.likes - post1.dislike)
        }

// В этом методе у меня по идее подгрузка. Но такой способ никак не подходит.
// Во первых при данном способе не поучается оргазивать LiveData.
// Во вторых криво работает сортировка данных, так как при подгрузки они перемешиваються с общем списком.
// В третьих каким образом организовать логику по вычислению следующих id (учитывать добавления нового поста, учитывать общее число строк_
// Вообщем я не очень представляю каким образом можно организовать подгрузку таким методом да и реально ли это вообще =)
    
    fun refreshPosts() {
        _state.value = FeedModel(loading = true)
        val totalItems = repository.count()
        if (totalItems > endPos) {
            startPos += STEP_LIMIT
            endPos += STEP_LIMIT
            _data.value = _data.value?.plus(repository.getRangePosts(startPos, endPos))
        }
        _state.value = FeedModel()
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