package com.strangecoder.socialmedia.data.entities

data class Message(
    val id: String = "",
    val content: String = "",
    val idFrom: String = "",
    val idTo: String = "",
    val read: Boolean = false,
    val timestamp: String = ""
)

data class LastMessage(
    val lastMessage: Message,
    val users: List<User>
)
