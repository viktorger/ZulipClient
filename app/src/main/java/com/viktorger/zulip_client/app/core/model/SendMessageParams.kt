package com.viktorger.zulip_client.app.core.model

data class SendMessageParams(
    val streamName: String,
    val topicName: String,
    val content: String
)
