package ru.netology.motivationapp.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.netology.motivationapp.BuildConfig
import ru.netology.motivationapp.R
import ru.netology.motivationapp.adapter.IOnInteractionListener
import ru.netology.motivationapp.adapter.PostsAdapter
import ru.netology.motivationapp.databinding.FragmentAuthorListBinding
import ru.netology.motivationapp.dto.Post
import ru.netology.motivationapp.swipecontroller.IOnSwipeControllerActions
import ru.netology.motivationapp.swipecontroller.SwipeButton
import ru.netology.motivationapp.swipecontroller.SwipeHelper
import ru.netology.motivationapp.utils.StringArg
import ru.netology.motivationapp.viewmodel.PostViewModel
import java.io.File
import java.io.FileNotFoundException

class AuthorListFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

//    companion object {
//        var Bundle.authorFilter: String? by StringArg
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthorListBinding.inflate(layoutInflater)
        val authorFilter = arguments?.let { AuthorListFragmentArgs.fromBundle(it).authorFilter }
        val adapter = PostsAdapter(object : IOnInteractionListener {
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
                                "images"
                            )
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
                                val action = AuthorListFragmentDirections
                                    .actionAuthorListFragmentToCreatePostFragment(
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
        binding.rvAuthorPostList.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(
                posts
                    .asSequence()
                    .filter { it.author == authorFilter }
                    .sortedWith(compareBy { it.dateCompare })
                    .sortedWith { post1, post2 ->
                        (post2.likes - post2.dislike) - (post1.likes - post1.dislike)
                    }
                    .toList()
            )
        }
        return binding.root
    }
}