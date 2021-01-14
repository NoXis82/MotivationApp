package ru.netology.motivationapp.fragments


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.netology.motivationapp.BuildConfig
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
import java.io.File
import java.io.FileNotFoundException


class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private var pageItemLimit = 20
    private val LIMIT_ITEMS_LIST = 20
    private var isLoading = false
    lateinit var adapter: PostsAdapter
    lateinit var binding: FeedFragmentBinding


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FeedFragmentBinding.inflate(layoutInflater)
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

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isLoading = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) {
                    val visibleItemCount = (recyclerView.layoutManager as LinearLayoutManager)
                            .childCount
                    val lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager)
                            .findLastVisibleItemPosition()
                    val totalItemCount = (recyclerView.layoutManager as LinearLayoutManager)
                            .itemCount
                    if (isLoading && (visibleItemCount + lastVisibleItem == totalItemCount)) {
                        isLoading = false
                        getPageItem()
                    }
                }
            }
        })

        return binding.root
    }

    private fun getPageItem() {
        binding.pbLoadList.visibility = View.VISIBLE
        Handler().postDelayed({
            if (this::adapter.isInitialized) {
                if (adapter.itemCount == pageItemLimit) {
                    pageItemLimit += LIMIT_ITEMS_LIST
                }
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
            } else {
                adapter = PostsAdapter(object : IOnInteractionListener {

                    override fun onLike(post: Post) {
                        viewModel.like(post.id)
                    }

                    override fun onDisLike(post: Post) {
                        viewModel.dislike(post.id)
                    }

                    override fun onShare(post: Post) {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, post.author + "\n" + post.content)
                            type = "text/plain"
                            if (post.pictureName != "") {
                                try {
                                    val fileDir = File(
                                            binding.root.context?.filesDir,
                                            "images")
                                    fileDir.mkdir()
                                    val file = File(fileDir.path, post.pictureName)
                                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    val uriImg = FileProvider.getUriForFile(
                                            requireContext(),
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
                                getString(R.string.chooser_share_post)
                        )
                        startActivity(shareIntent)
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
            }
            binding.pbLoadList.visibility = View.GONE
        }, 1000)
    }

}