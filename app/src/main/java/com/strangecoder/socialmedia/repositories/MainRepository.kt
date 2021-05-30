package com.strangecoder.socialmedia.repositories

import android.net.Uri
import com.strangecoder.socialmedia.other.Resource

interface MainRepository {

    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>
}