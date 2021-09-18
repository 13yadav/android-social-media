package com.strangecoder.socialmedia.commons

fun getConversationId(userID: String, peerID: String): String {
    return if (userID.hashCode() <= peerID.hashCode()) "${userID}_${peerID}" else "${peerID}_${userID}"
}