package ru.netology.motivationapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.motivationapp.adapter.IOnInteractionListener
import ru.netology.motivationapp.adapter.PostsAdapter
import ru.netology.motivationapp.databinding.FeedFragmentBinding
import ru.netology.motivationapp.dto.*
import ru.netology.motivationapp.viewmodel.PostViewModel

class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FeedFragmentBinding.inflate(layoutInflater)
        val adapter = PostsAdapter(object : IOnInteractionListener{
            override fun onLike(post: Post) {
                TODO("Not yet implemented")
            }

            override fun onDisLike(post: Post) {
                TODO("Not yet implemented")
            }

            override fun onShare(post: Post) {
                TODO("Not yet implemented")
            }

            override fun onRemove(post: Post) {
                TODO("Not yet implemented")
            }

            override fun onEdit(post: Post) {
                TODO("Not yet implemented")
            }

        })

        binding.fab.setOnClickListener {
            TODO("Not yet implemented")
        }

        binding.rvPostList.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }
        return binding.root

    }


}