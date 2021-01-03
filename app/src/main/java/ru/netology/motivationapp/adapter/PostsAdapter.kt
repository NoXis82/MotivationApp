package ru.netology.motivationapp.adapter

import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.motivationapp.databinding.PostCardBinding
import ru.netology.motivationapp.dto.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class PostsAdapter(

    private val onInteractionListener: IOnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val postView = PostCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(postView, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

}

class PostViewHolder(
    private val binding: PostCardBinding,
    private val onInteractionListener: IOnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.datePublished
            content.text = post.content
            if (post.pictureName == "") {
                binding.ivImageView.visibility = View.GONE
            } else {
                binding.ivImageView.visibility = View.VISIBLE
                try {
                    val fileDir = File(binding.root.context?.filesDir, "Images")
                    fileDir.mkdir()
                    val file = File(fileDir, post.pictureName)
                    val bitmap = BitmapFactory.decodeFile(file.toString())
                    binding.ivImageView.setImageBitmap(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
}


class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}
