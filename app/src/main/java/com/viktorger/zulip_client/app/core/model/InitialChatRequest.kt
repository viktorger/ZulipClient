package com.viktorger.zulip_client.app.core.model

import com.viktorger.zulip_client.app.core.common.PAGE_SIZE

data class InitialChatRequest(
    val streamName: String,
    val topicName: String,
    val pageSize: Int = PAGE_SIZE
)
