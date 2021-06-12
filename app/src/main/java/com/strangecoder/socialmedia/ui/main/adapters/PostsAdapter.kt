package com.strangecoder.socialmedia.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.data.entities.Post
import com.strangecoder.socialmedia.databinding.ListItemPostBinding
import javax.inject.Inject

class PostsAdapter @Inject constructor(
    private val glide: RequestManager,
    private val interaction: Interaction
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var posts: List<Post>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            ListItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            glide,
            interaction
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    class PostViewHolder(
        private val binding: ListItemPostBinding,
        private val glide: RequestManager,
        private val interaction: Interaction
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            glide.load(post.imageUrl).into(binding.ivPostImage)
            glide.load(post.authorProfilePictureUrl).into(binding.ivAuthorProfileImage)
            binding.tvPostAuthor.text = post.authorUsername
            binding.tvPostText.text = post.text
            val likeCount = post.likedBy.size
            binding.tvLikedBy.text = when {
                likeCount <= 0 -> "no likes"
                likeCount == 1 -> "liked by 1 person"
                else -> "liked by $likeCount people"
            }
            val uid = FirebaseAuth.getInstance().uid!!
            binding.ibDeletePost.isVisible = uid == post.authorUid
            binding.ibLike.setImageResource(
                if (post.isLiked) {
                    R.drawable.ic_heart_filled
                } else R.drawable.ic_heart_outline
            )

            binding.tvPostAuthor.setOnClickListener {
                interaction.onUserClickListener(post.authorUid, adapterPosition)
            }
            binding.ivAuthorProfileImage.setOnClickListener {
                interaction.onUserClickListener(post.authorUid, adapterPosition)
            }
            binding.tvLikedBy.setOnClickListener {
                interaction.onLikedByClickListener(post, adapterPosition)
            }
            binding.ibLike.setOnClickListener {
                interaction.onLikeClickListener(post, adapterPosition)
            }
            binding.ibComments.setOnClickListener {
                interaction.onCommentsClickListener(post, adapterPosition)
            }
            binding.ibDeletePost.setOnClickListener {
                interaction.onDeletePostClickListener(post, adapterPosition)
            }
        }
    }

    interface Interaction {
        fun onLikeClickListener(item: Post, position: Int)
        fun onUserClickListener(uid: String, position: Int)
        fun onDeletePostClickListener(item: Post, position: Int)
        fun onLikedByClickListener(item: Post, position: Int)
        fun onCommentsClickListener(item: Post, position: Int)
    }
}