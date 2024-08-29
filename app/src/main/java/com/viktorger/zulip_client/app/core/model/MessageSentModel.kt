package com.viktorger.zulip_client.app.core.model

data class MessageSentModel(
    val messageId: Int,
    val timestamp: Long,
    val content: String,
    val reactionsWithCounter: List<ReactionWithCounter>
)
