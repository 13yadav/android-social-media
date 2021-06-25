package com.strangecoder.socialmedia.other

fun getConversationId(userID: String, peerID: String): String {
    return if (userID.hashCode() <= peerID.hashCode()) "${userID}_${peerID}" else "${peerID}_${userID}"
}