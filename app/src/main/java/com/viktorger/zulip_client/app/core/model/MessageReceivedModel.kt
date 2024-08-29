package com.viktorger.zulip_client.app.core.model

data class MessageReceivedModel(
    val messageId: Int,
    val userId: Int,
    val username: String,
    val timestamp: Long,
    val content: String,
    val avatarUrl: String?,
    val reactionsWithCounter: List<ReactionWithCounter>
)
