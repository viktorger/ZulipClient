package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class MessagesList(
    val messages: List<MessageModelNetwork>
)
