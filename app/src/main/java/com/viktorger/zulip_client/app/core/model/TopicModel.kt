package com.viktorger.zulip_client.app.core.model

data class TopicModel (
    val streamName: String,
    val topicName: String,
    val messagesUnread: Int
)