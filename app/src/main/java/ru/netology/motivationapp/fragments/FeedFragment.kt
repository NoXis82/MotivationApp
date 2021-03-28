package ru.netology.motivationapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
            viewModel.refreshPosts()
        }
        binding.rvPostList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(viewModel.sortedList(it))
        }

        viewModel.state.observe(viewLifecycleOwner) { model ->
            binding.swipeRefreshLayout.isRefreshing = model.refreshing
            binding.pbProgress.isVisible = model.loading
        }
        return binding.root
    }
}
