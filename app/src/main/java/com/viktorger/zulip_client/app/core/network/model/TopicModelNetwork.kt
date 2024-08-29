package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopicModelNetwork(
    val name: String,
    @SerialName("max_id") val maxMessageId: Int,
)
