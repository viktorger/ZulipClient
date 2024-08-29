package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventResponse(
    val events: List<EventNetwork>
)

@Serializable
data class EventNetwork(
    val type: String,
    val op: String? = null,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("reaction_type") val reactionType: String? = null,
    @SerialName("emoji_code") val emojiCode: String? = null,
    @SerialName("message_id") val messageId: Int? = null,
    val message: MessageModelNetwork? = null,
    @SerialName("id") val eventId: Int
)

