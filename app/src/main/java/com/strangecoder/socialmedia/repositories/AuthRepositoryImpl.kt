package com.strangecoder.socialmedia.repositories

import com.google.firebase.auth.AuthResult
import com.strangecoder.socialmedia.other.Resource

class AuthRepositoryImpl : AuthRepository {

    override suspend fun registerUser(
        email: String,
        username: String,
        password: String
    ): Resource<AuthResult> {
        TODO("Not yet implemented")
    }

    override suspend fun loginUser(email: String, password: String): Resource<AuthResult> {
        TODO("Not yet implemented")
    }
}