package ru.netology.motivationapp.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.motivationapp.databinding.PostCardBinding
import ru.netology.motivationapp.dto.*
import java.io.File
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
            tvAuthor.text = post.author
            tvPublished.text = post.datePublished
            tvContent.text = post.content
            btnLikes.text = formatCountToStr(post.likes)
            btnDislike.text = formatCountToStr(post.dislike)
            if ((post.likes - post.dislike) >= 0) {
                tvRatingValue.setTextColor(Color.GREEN)
            } else {
                tvRatingValue.setTextColor(Color.RED)
            }
            tvRatingValue.text = (post.likes - post.dislike).toString()
            if (post.pictureName == "") {
                ivImageView.visibility = View.GONE
            } else {
                ivImageView.visibility = View.VISIBLE
                try {
                    val fileDir = File(root.context?.filesDir, "images")
                    fileDir.mkdir()
                    val file = File(fileDir, post.pictureName)
                    val bitmap = BitmapFactory.decodeFile(file.toString())
                    ivImageView.setImageBitmap(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
            tvAuthor.setOnClickListener {
                onInteractionListener.onPostAuthorClick(post)
            }
            ivAvatar.setOnClickListener {
                onInteractionListener.onPostAuthorClick(post)
            }
            btnLikes.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            btnDislike.setOnClickListener {
                onInteractionListener.onDisLike(post)
            }
            btnShare.setOnClickListener {
                onInteractionListener.onShare(post)
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

private fun formatCountToStr(value: Int): String {
    return when (value / 1000) {
        0 -> "$value"
        in 1..9 -> {
            val str = "%.1f".format(value / 1000.0)
                .dropLastWhile { it == '0' }
                .dropLastWhile { it == '.' }
            "${str}K"
        }
        in 10..999 -> {
            val res = value / 1000
            "${res}K"
        }
        else -> {
            val str = "%.1f".format(value / 1000000.0)
                .dropLastWhile { it == '0' }
                .dropLastWhile { it == '.' }
            "${str}М"
        }
    }
}
