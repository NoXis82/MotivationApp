package ru.netology.motivationapp.fragments


import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.netology.motivationapp.R
import ru.netology.motivationapp.fragments.CreatePostFragment.Companion.author
import ru.netology.motivationapp.fragments.CreatePostFragment.Companion.content
import ru.netology.motivationapp.fragments.CreatePostFragment.Companion.pictureName
import ru.netology.motivationapp.fragments.AuthorListFragment.Companion.authorFilter
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
    private val pageItemLimit = 5
    private var isLoading = false
    private var numberPage = 1

    lateinit var adapter: PostsAdapter
lateinit var binding: FeedFragmentBinding


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        getPageItem()

        object : SwipeHelper(requireContext(), binding.rvPostList, 200) {
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
                                    R.id.action_feedFragment_to_createPostFragment,
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

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_createPostFragment)
        }

        binding.rvPostList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = (recyclerView.layoutManager as LinearLayoutManager).childCount
                val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager)
                    .findFirstCompletelyVisibleItemPosition()
                val totalItemCount = (recyclerView.layoutManager as LinearLayoutManager).itemCount
                if (!isLoading) {
                    Toast.makeText(
                        requireContext(),
                        "vis: $visibleItemCount , first: $firstVisibleItem , tot:$totalItemCount",
                        Toast.LENGTH_LONG
                    ).show()
                    if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                        Toast.makeText(requireContext(), "onScroll fun", Toast.LENGTH_SHORT)
                            .show()
                        numberPage++
                        getPageItem()
                    }
                }
            }

        })

        return binding.root
    }

    private fun getPageItem() {
        isLoading = true
        binding = FeedFragmentBinding.inflate(layoutInflater)
        val rangeItem = numberPage * pageItemLimit

        binding.pbLoadList.visibility = View.VISIBLE
        Handler().postDelayed({
            if (this::adapter.isInitialized) {
                adapter.notifyDataSetChanged()
            } else {
                adapter = PostsAdapter(object : IOnInteractionListener {
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
                        findNavController().navigate(
                            R.id.action_feedFragment_to_authorListFragment,
                            Bundle().apply {
                                authorFilter = post.author
                            }

                        )

                    }

                })
                binding.rvPostList.adapter = adapter
            }
            isLoading = false
            binding.pbLoadList.visibility = View.GONE
            viewModel.data.observe(viewLifecycleOwner) { posts ->
                adapter.submitList(
                    posts
                        .drop(0)
                        .take(rangeItem)
                        .sortedWith(compareBy { it.dateCompare })
                )
            }

        }, 1500)
    }

}