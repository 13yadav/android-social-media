package com.strangecoder.socialmedia.other

inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (th: Throwable) {
        Resource.Error(th.message ?: "An unknown error occurred!")
    }
}