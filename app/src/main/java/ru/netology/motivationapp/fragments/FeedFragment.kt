package ru.netology.motivationapp.fragments


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.netology.motivationapp.R
import ru.netology.motivationapp.adapter.IOnInteractionListener
import ru.netology.motivationapp.adapter.PostsAdapter
import ru.netology.motivationapp.databinding.FeedFragmentBinding
import ru.netology.motivationapp.dto.*
import ru.netology.motivationapp.swipecontroller.IOnSwipeControllerActions
import ru.netology.motivationapp.swipecontroller.SwipeButton
import ru.netology.motivationapp.swipecontroller.SwipeHelper
import ru.netology.motivationapp.viewmodel.PostViewModel

class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private var pageItemLimit = 1
    private val LIMIT_ITEMS_LIST = 1
    lateinit var adapter: PostsAdapter
    lateinit var binding: FeedFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FeedFragmentBinding.inflate(layoutInflater)
        adapter = PostsAdapter(object : IOnInteractionListener {

            override fun onLike(post: Post) {
                viewModel.like(post.id)
            }

            override fun onDisLike(post: Post) {
                viewModel.dislike(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.sharePost(post)
            }

            override fun onPostAuthorClick(post: Post) {
                val action = FeedFragmentDirections.actionFeedFragmentToAuthorListFragment(
                    authorFilter = post.author
                )
                findNavController().navigate(action)
            }
        })
        getPageItem()
        object : SwipeHelper(requireContext(), binding.rvPostList, 200) {
            override fun instantiateSwipeButtons(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<SwipeButton>
            ) {
                buffer.add(
                    SwipeButton(
                        requireContext(),
                        getString(R.string.delete),
                        R.drawable.ic_delete_24,
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
                        getString(R.string.edit),
                        R.drawable.ic_edit_24,
                        Color.parseColor("#FF9502"),
                        object : IOnSwipeControllerActions {
                            override fun onClick(pos: Int) {
                                viewModel.editPost(adapter.currentList[pos])
                                val action = FeedFragmentDirections
                                    .actionFeedFragmentToCreatePostFragment(
                                        author = adapter.currentList[pos].author,
                                        content = adapter.currentList[pos].content,
                                        pictureName = adapter.currentList[pos].pictureName
                                    )
                                findNavController().navigate(action)
                            }
                        }
                    )
                )
            }
        }
        binding.fabAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_createPostFragment)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            pageItemLimit += LIMIT_ITEMS_LIST
            getPageItem()
        }
        return binding.root
    }

    private fun getPageItem() {
        binding.rvPostList.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(
                posts
                    .asSequence()
                    .sortedWith(compareBy { it.dateCompare })
                    .sortedWith { post1, post2 ->
                        (post2.likes - post2.dislike) - (post1.likes - post1.dislike)
                    }
                    .toList()
                    .takeLast(pageItemLimit)
            )
        }
        binding.swipeRefreshLayout.isRefreshing = false
    }
}