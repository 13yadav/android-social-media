package com.strangecoder.socialmedia.commons

import android.util.Log

inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (th: Throwable) {
        Log.e("rrLOG", th.toString())
        Resource.Error(th.localizedMessage ?: "An unknown error occurred!")
    }
}