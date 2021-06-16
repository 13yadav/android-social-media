package com.strangecoder.socialmedia.other

import android.util.Log

inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (th: Throwable) {
        Log.e("rrLOG", th.toString())
        Log.e("rrLOG", th.message.toString())
        Resource.Error(th.message ?: "An unknown error occurred!")
    }
}