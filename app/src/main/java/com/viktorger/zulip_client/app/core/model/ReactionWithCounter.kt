package com.viktorger.zulip_client.app.core.model

data class ReactionWithCounter(
    val emoji: String,
    val count: Int,
    val isSelected: Boolean
)
