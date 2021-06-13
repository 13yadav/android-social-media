package com.strangecoder.socialmedia.repositories

import android.net.Uri
import com.strangecoder.socialmedia.data.entities.Post
import com.strangecoder.socialmedia.data.entities.User
import com.strangecoder.socialmedia.other.Resource

interface MainRepository {

    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>

    suspend fun getUsers(uids: List<String>): Resource<List<User>>

    suspend fun getUser(uid: String): Resource<User>

    suspend fun getPostsForFollows(): Resource<List<Post>>

    suspend fun getPostForProfile(uid: String): Resource<List<Post>>

    suspend fun toggleLikeForPost(post: Post): Resource<Boolean>

    suspend fun deletePost(post: Post): Resource<Post>

    suspend fun toggleFollowForUser(uid: String): Resource<Boolean>

    suspend fun searchUser(query: String): Resource<List<User>>
}