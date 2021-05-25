package com.strangecoder.socialmedia.repositories

import com.google.firebase.auth.AuthResult
import com.strangecoder.socialmedia.other.Resource

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