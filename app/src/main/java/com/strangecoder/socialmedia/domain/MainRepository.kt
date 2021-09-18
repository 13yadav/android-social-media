package com.strangecoder.socialmedia.domain

import android.net.Uri
import com.strangecoder.socialmedia.data.entities.*
import com.strangecoder.socialmedia.commons.Resource

interface MainRepository {

    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>

    suspend fun getAllUsers(): Resource<List<User>>

    suspend fun getUsers(uids: List<String>): Resource<List<User>>

    suspend fun getUser(uid: String): Resource<User>

    suspend fun getPostsForFollows(): Resource<List<Post>>

    suspend fun getPostForProfile(uid: String): Resource<List<Post>>

    suspend fun toggleLikeForPost(post: Post): Resource<Boolean>

    suspend fun deletePost(post: Post): Resource<Post>

    suspend fun toggleFollowForUser(uid: String): Resource<Boolean>

    suspend fun searchUser(query: String): Resource<List<User>>

    suspend fun createComment(commentText: String, postId: String): Resource<Comment>

    suspend fun getCommentForPost(postId: String): Resource<List<Comment>>

    suspend fun deleteComment(comment: Comment): Resource<Comment>

    suspend fun updateProfile(profileUpdate: ProfileUpdate): Resource<Any>

    suspend fun updateProfilePicture(uid: String, imageUri: Uri): Uri?

    suspend fun sendMessage(message: Message): Resource<Message>

    suspend fun loadMessages(idFrom: String, idTo: String): Resource<List<Message>>

    suspend fun updateLastMessage(
        idFrom: String,
        idTo: String,
        lastMessage: LastMessage
    ): Resource<Any>

    suspend fun getLastMessage(idFrom: String, idTo: String): Resource<LastMessage>
}