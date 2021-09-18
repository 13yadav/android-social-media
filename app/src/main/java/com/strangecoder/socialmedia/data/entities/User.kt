package com.strangecoder.socialmedia.data.entities

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.strangecoder.socialmedia.commons.Constants.DEFAULT_PROFILE_PICTURE_URL

@IgnoreExtraProperties
data class User(
    val uid: String = "",
    val username: String = "",
    val profilePictureUrl: String = DEFAULT_PROFILE_PICTURE_URL,
    val bio: String = "",
    var follows: List<String> = listOf(),
    @get:Exclude
    var isFollowing: Boolean = false
)
