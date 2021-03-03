package ru.netology.motivationapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import ru.netology.motivationapp.App
import ru.netology.motivationapp.BuildConfig
import ru.netology.motivationapp.R
import ru.netology.motivationapp.adapter.PostsAdapter
import ru.netology.motivationapp.dto.Post
import ru.netology.motivationapp.fragments.FeedFragment
import ru.netology.motivationapp.fragments.FeedFragmentDirections
import ru.netology.motivationapp.swipecontroller.IOnSwipeControllerActions
import ru.netology.motivationapp.swipecontroller.SwipeButton
import ru.netology.motivationapp.swipecontroller.SwipeHelper
import java.io.File
import java.io.FileNotFoundException

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val empty = Post(
        id = 0,
        author = "",
        content = "",
        datePublished = "",
        pictureName = "",
        dateCompare = 0
    )
    private val repository = App.repositorySQLite
    val data = repository.getAll()
    private val edited = MutableLiveData(empty)
    fun remove(id: Long) = repository.removePost(id)
    fun like(id: Long) = repository.like(id)
    fun dislike(id: Long) = repository.dislike(id)
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

    fun InitSwipeHelper(
        context: Context,
        recyclerView: RecyclerView,
        buttonWidth: Int,
        adapter: PostsAdapter,
        view: View
    ): SwipeHelper = object : SwipeHelper(context, recyclerView, buttonWidth, adapter, view) {
        override fun instantiateSwipeButtons(
            viewHolder: RecyclerView.ViewHolder,
            buffer: MutableList<SwipeButton>
        ) {
            buffer.add(
                SwipeButton(
                    context,
                    context.getString(R.string.delete),
                    R.drawable.ic_delete_24,
                    Color.parseColor("#FF3C30"),
                    object : IOnSwipeControllerActions {
                        override fun onClick(pos: Int) {
                            remove(adapter.currentList[pos].id)
                            adapter.notifyItemRemoved(pos)
                            adapter.notifyItemRangeChanged(pos, adapter.itemCount)
                        }
                    }
                )
            )
            buffer.add(
                SwipeButton(
                    context,
                    context.getString(R.string.edit),
                    R.drawable.ic_edit_24,
                    Color.parseColor("#FF9502"),
                    object : IOnSwipeControllerActions {
                        override fun onClick(pos: Int) {
                            editPost(adapter.currentList[pos])
                            val action = FeedFragmentDirections
                                .actionFeedFragmentToCreatePostFragment(
                                    author = adapter.currentList[pos].author,
                                    content = adapter.currentList[pos].content,
                                    pictureName = adapter.currentList[pos].pictureName
                                )
                         //   Navigation.findNavController(view).navigate(action)
                            
//                             findNavController().navigate(action)
                        }
                    }
                )
            )
        }
    }


}