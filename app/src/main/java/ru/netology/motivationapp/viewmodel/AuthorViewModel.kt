package ru.netology.motivationapp.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import ru.netology.motivationapp.R
import ru.netology.motivationapp.adapter.PostsAdapter
import ru.netology.motivationapp.fragments.AuthorListFragmentDirections
import ru.netology.motivationapp.swipecontroller.IOnSwipeControllerActions
import ru.netology.motivationapp.swipecontroller.SwipeButton
import ru.netology.motivationapp.swipecontroller.SwipeHelper

class AuthorViewModel(application: Application): BaseViewModel(application) {

    fun InitSwipeHelper(
        context: Context,
        recyclerView: RecyclerView,
        buttonWidth: Int,
        adapter: PostsAdapter,
        navigationFragments: NavController
    ): SwipeHelper =
        object : SwipeHelper(context, recyclerView, buttonWidth, adapter, navigationFragments) {
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
                                val action = AuthorListFragmentDirections
                                    .actionAuthorListFragmentToCreatePostFragment(
                                        author = adapter.currentList[pos].author,
                                        content = adapter.currentList[pos].content,
                                        pictureName = adapter.currentList[pos].pictureName
                                    )
                                navigationFragments.navigate(action)
                            }
                        }
                    )
                )
            }
        }
}