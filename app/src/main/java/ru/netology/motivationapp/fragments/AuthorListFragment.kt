package ru.netology.motivationapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.motivationapp.adapter.IOnInteractionListener
import ru.netology.motivationapp.adapter.PostsAdapter
import ru.netology.motivationapp.databinding.FragmentAuthorListBinding
import ru.netology.motivationapp.dto.Post
import ru.netology.motivationapp.viewmodel.AuthorViewModel

class AuthorListFragment : Fragment() {
    private val viewModel: AuthorViewModel by viewModels(ownerProducer = ::requireParentFragment)

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
                viewModel.sharePost(post)
            }

            override fun onPostAuthorClick(post: Post) {
                Toast.makeText(requireContext(), post.author, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.InitSwipeHelper(
            requireContext(),
            binding.rvAuthorPostList,
            200,
            adapter,
            findNavController()
        )
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