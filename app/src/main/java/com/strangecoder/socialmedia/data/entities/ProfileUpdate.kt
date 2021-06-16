package com.strangecoder.socialmedia.data.entities

import android.net.Uri

data class ProfileUpdate(
    val uidToUpdate: String = "",
    val username: String = "",
    val bio: String = "",
    val profilePictureUri: Uri? = null
)
