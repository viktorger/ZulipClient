package com.viktorger.zulip_client.app.core.model

data class MessagePageLoadAnswer (
    val chat: ChatModel,
    val limitReached: Boolean
)
