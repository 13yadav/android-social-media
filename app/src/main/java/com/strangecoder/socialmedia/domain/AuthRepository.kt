package com.strangecoder.socialmedia.domain

import com.google.firebase.auth.AuthResult
import com.strangecoder.socialmedia.commons.Resource

interface AuthRepository {

    suspend fun registerUser(
        email: String,
        username: String,
        password: String
    ): Resource<AuthResult>

    suspend fun loginUser(
        email: String,
        password: String
    ): Resource<AuthResult>
}