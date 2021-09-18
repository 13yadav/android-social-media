package com.strangecoder.socialmedia.presentation.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.strangecoder.socialmedia.data.entities.User
import com.strangecoder.socialmedia.databinding.ListItemUserBinding
import javax.inject.Inject

class UserAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var users: List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            ListItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            glide
        )
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
        holder.binding.root.setOnClickListener {
            onUserClickListener?.let { click ->
                click(user)
            }
        }
    }

    class UserViewHolder(
        val binding: ListItemUserBinding,
        private val glide: RequestManager
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            glide.load(user.profilePictureUrl).into(binding.ivProfileImage)
            binding.tvUsername.text = user.username
            if (user.bio.isNotBlank()) binding.tvBio.text = user.bio
            else binding.tvBio.isVisible = false
        }
    }

    private var onUserClickListener: ((User) -> Unit)? = null

    fun setOnUserClickListener(listener: (User) -> Unit) {
        onUserClickListener = listener
    }

}