package ru.netology.motivationapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.motivationapp.R
import ru.netology.motivationapp.adapter.IOnInteractionListener
import ru.netology.motivationapp.adapter.PostsAdapter
import ru.netology.motivationapp.databinding.FeedFragmentBinding
import ru.netology.motivationapp.dto.*
import ru.netology.motivationapp.viewmodel.FeedViewModel

class FeedFragment : Fragment() {
    private val viewModel: FeedViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private var pageItemLimit = 2
    private val MAX_LIMIT_ITEMS = 20
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
                viewModel.authorListFilter(post, findNavController())
            }
        })
        getPageItem()
        viewModel.InitSwipeHelper(
            requireContext(),
            binding.rvPostList,
            200,
            adapter,
            findNavController()
        )
        binding.fabAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_createPostFragment)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            //pageItemLimit += MAX_LIMIT_ITEMS
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
                    .takeLast(posts.size)
            )
        }
        binding.swipeRefreshLayout.isRefreshing = false
    }
}