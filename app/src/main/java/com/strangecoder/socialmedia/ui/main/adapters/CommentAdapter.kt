package com.strangecoder.socialmedia.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.data.entities.Comment
import com.strangecoder.socialmedia.databinding.ListIemCommentBinding
import javax.inject.Inject

class CommentAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Comment>() {
        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.commentId == newItem.commentId
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var comments: List<Comment>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            ListIemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            glide
        )
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
        holder.binding.apply {
            tvCommentUsername.setOnClickListener {
                onUserClickListener?.let { click -> click(comment) }
            }
            ibDeleteComment.setOnClickListener {
                onDeleteCommentClickListener?.let { click -> click(comment) }
            }
        }
    }

    class CommentViewHolder(
        val binding: ListIemCommentBinding,
        private val glide: RequestManager
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment) {
            glide.load(comment.profilePictureUrl).into(binding.ivCommentUserProfilePicture)
            binding.ibDeleteComment.isVisible = comment.uid == FirebaseAuth.getInstance().uid!!
            binding.tvComment.text = comment.comment
            binding.tvCommentUsername.text = comment.username
        }
    }

    private var onUserClickListener: ((Comment) -> Unit)? = null
    private var onDeleteCommentClickListener: ((Comment) -> Unit)? = null

    fun setOnUserClickListener(listener: (Comment) -> Unit) {
        onUserClickListener = listener
    }

    fun setOnDeleteCommentClickListener(listener: (Comment) -> Unit) {
        onDeleteCommentClickListener = listener
    }

}