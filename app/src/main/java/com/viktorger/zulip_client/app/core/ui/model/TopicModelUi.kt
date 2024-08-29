package com.viktorger.zulip_client.app.core.ui.model

data class TopicModelUi(
    val topicName: String,
    val streamName: String,
    val messagesCount: Int,
    val color: Int
)