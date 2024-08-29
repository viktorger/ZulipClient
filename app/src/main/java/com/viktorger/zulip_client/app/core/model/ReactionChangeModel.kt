package com.viktorger.zulip_client.app.core.model

data class ReactionChangeModel (
    val messageId: Int,
    val emoji: String,
    val isOwnUser: Boolean,
    val isAdded: Boolean
)