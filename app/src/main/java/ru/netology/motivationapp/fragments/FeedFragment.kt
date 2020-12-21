package ru.netology.motivationapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.netology.motivationapp.R
import ru.netology.motivationapp.adapter.IOnInteractionListener
import ru.netology.motivationapp.adapter.PostsAdapter
import ru.netology.motivationapp.databinding.FeedFragmentBinding
import ru.netology.motivationapp.dto.*
import ru.netology.motivationapp.swipecontroller.SwipeController
import ru.netology.motivationapp.viewmodel.PostViewModel

class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val swipeController  = SwipeController()
    private val itemTouchHelper = ItemTouchHelper(swipeController)



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FeedFragmentBinding.inflate(layoutInflater)
       itemTouchHelper.attachToRecyclerView(binding.rvPostList)


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
        findNavController().navigate(R.id.action_feedFragment_to_createPostFragment)
        }

        binding.rvPostList.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }
        return binding.root

    }

}