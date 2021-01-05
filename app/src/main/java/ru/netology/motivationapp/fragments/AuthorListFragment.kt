package ru.netology.motivationapp.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.netology.motivationapp.R
import ru.netology.motivationapp.adapter.IOnInteractionListener
import ru.netology.motivationapp.adapter.PostsAdapter
import ru.netology.motivationapp.databinding.FragmentAuthorListBinding
import ru.netology.motivationapp.dto.Post
import ru.netology.motivationapp.fragments.CreatePostFragment.Companion.author
import ru.netology.motivationapp.fragments.CreatePostFragment.Companion.content
import ru.netology.motivationapp.fragments.CreatePostFragment.Companion.pictureName
import ru.netology.motivationapp.swipecontroller.IOnSwipeControllerActions
import ru.netology.motivationapp.swipecontroller.SwipeButton
import ru.netology.motivationapp.swipecontroller.SwipeHelper
import ru.netology.motivationapp.utils.StringArg
import ru.netology.motivationapp.viewmodel.PostViewModel

class AuthorListFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    companion object {
        var Bundle.authorFilter: String? by StringArg
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAuthorListBinding.inflate(layoutInflater)
        val adapter = PostsAdapter(object : IOnInteractionListener {
            override fun onLike(post: Post) {
                TODO("Not yet implemented")
            }

            override fun onDisLike(post: Post) {
                TODO("Not yet implemented")
            }

            override fun onShare(post: Post) {
                TODO("Not yet implemented")
            }

            override fun onPostAuthorClick(post: Post) {

            }
     })

        object : SwipeHelper(requireContext(), binding.rvAuthorPostList, 200) {
            override fun instantiateSwipeButtons(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<SwipeButton>
            ) {
                buffer.add(
                    SwipeButton(
                        requireContext(),
                        "Delete",
                        0,
                        Color.parseColor("#FF3C30"),
                        object : IOnSwipeControllerActions {
                            override fun onClick(pos: Int) {
                                viewModel.remove(adapter.currentList[pos].id)
                                adapter.notifyItemRemoved(pos)
                                adapter.notifyItemRangeChanged(pos, adapter.itemCount)
                            }

                        }
                    )
                )
                buffer.add(
                    SwipeButton(
                        requireContext(),
                        "Edit",
                        0,
                        Color.parseColor("#FF9502"),
                        object : IOnSwipeControllerActions {
                            override fun onClick(pos: Int) {
                                viewModel.editPost(adapter.currentList[pos])
                                findNavController().navigate(
                                    R.id.action_authorListFragment_to_createPostFragment,
                                    Bundle().apply {
                                        author = adapter.currentList[pos].author
                                        content = adapter.currentList[pos].content
                                        pictureName = adapter.currentList[pos].pictureName
                                    })

                            }

                        }
                    )
                )
            }

        }

        binding.rvAuthorPostList.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts
                .filter { it.author == arguments?.authorFilter }
                .sortedWith(compareBy { it.dateCompare }
                )
            )
        }
    return binding.root
    }
}