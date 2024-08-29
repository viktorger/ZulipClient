package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class MessagesEmptyList(
    val messages: List<Empty>
)

@Serializable
class Empty