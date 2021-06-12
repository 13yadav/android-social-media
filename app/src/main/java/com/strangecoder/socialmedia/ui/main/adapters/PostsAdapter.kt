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
    private val glide: RequestManager
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
            glide
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val binding = holder.binding
        val post = posts[position]
        holder.bind(post)

        binding.tvPostAuthor.setOnClickListener {
            onUserClickListener?.let { click ->
                click(post.authorUid)
            }
        }
        binding.ivAuthorProfileImage.setOnClickListener {
            onUserClickListener?.let { click ->
                click(post.authorUid)
            }
        }
        binding.tvLikedBy.setOnClickListener {
            onLikedByClickListener?.let { click ->
                click(post)
            }
        }
        binding.ibLike.setOnClickListener {
            onLikeClickListener?.let { click ->
                if (!post.isLiking) click(post, holder.layoutPosition)
            }
        }
        binding.ibComments.setOnClickListener {
            onCommentsClickListener?.let { click ->
                click(post)
            }
        }
        binding.ibDeletePost.setOnClickListener {
            onDeletePostClickListener?.let { click ->
                click(post)
            }
        }
    }

    override fun getItemCount() = posts.size

    class PostViewHolder(
        val binding: ListItemPostBinding,
        private val glide: RequestManager,
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
        }
    }

    private var onLikeClickListener: ((Post, Int) -> Unit)? = null
    private var onUserClickListener: ((String) -> Unit)? = null
    private var onDeletePostClickListener: ((Post) -> Unit)? = null
    private var onLikedByClickListener: ((Post) -> Unit)? = null
    private var onCommentsClickListener: ((Post) -> Unit)? = null

    fun setOnLikeClickListener(listener: (Post, Int) -> Unit) {
        onLikeClickListener = listener
    }

    fun setOnUserClickListener(listener: (String) -> Unit) {
        onUserClickListener = listener
    }

    fun setOnDeletePostClickListener(listener: (Post) -> Unit) {
        onDeletePostClickListener = listener
    }

    fun setOnLikedByClickListener(listener: (Post) -> Unit) {
        onLikedByClickListener = listener
    }

    fun setOnCommentsClickListener(listener: (Post) -> Unit) {
        onCommentsClickListener = listener
    }
}