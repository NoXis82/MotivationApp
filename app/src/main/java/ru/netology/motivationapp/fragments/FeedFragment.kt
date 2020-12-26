package ru.netology.motivationapp.fragments


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = FeedFragmentBinding.inflate(layoutInflater)

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

        val swipe = object : SwipeHelper(requireContext(), binding.rvPostList, 200) {
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
                                        Toast.makeText(
                                                requireContext(),
                                                "Delete post id: $pos",
                                                Toast.LENGTH_LONG)
                                                .show()
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
                                        Toast.makeText(
                                                requireContext(),
                                                "Edit post id: $pos",
                                                Toast.LENGTH_LONG)
                                                .show()
                                    }

                                }
                        )
                )
            }

        }

        return binding.root

    }

}