package com.strangecoder.socialmedia.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.data.entities.Message
import com.strangecoder.socialmedia.databinding.ListItemOthersMessageBinding
import com.strangecoder.socialmedia.databinding.ListItemSelfMessageBinding
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MessagesAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_OWN = 0
    private val TYPE_OTHER = 1

    private val diffCallback = object : DiffUtil.ItemCallback<Message>() {
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var messages: List<Message>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_OWN) {
            return OwnMessageViewHolder(
                ListItemSelfMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return OthersMessageViewHolder(
                ListItemOthersMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].idFrom == FirebaseAuth.getInstance().uid!!) {
            TYPE_OWN
        } else TYPE_OTHER
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = messages[position]
        if (holder is OwnMessageViewHolder) holder.bind(user)
        else if (holder is OthersMessageViewHolder) holder.bind(user)
    }

    class OwnMessageViewHolder(
        val binding: ListItemSelfMessageBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        fun bind(message: Message) {
            binding.textMessage.text = message.content
            binding.messageTime.isVisible = false
//            val time = sdf.format(message.timestamp.toLong())
//            binding.messageTime.text = time
        }
    }

    class OthersMessageViewHolder(
        val binding: ListItemOthersMessageBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

        fun bind(message: Message) {
            binding.textMessage.text = message.content
            binding.messageTime.isVisible = false
//            val time = sdf.format(message.timestamp.toLong())
//            binding.messageTime.text = time
        }
    }
}