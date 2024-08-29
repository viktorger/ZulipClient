package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageModelNetwork(
    @SerialName("id") val messageId: Int,
    @SerialName("sender_id") val userId: Int,
    @SerialName("sender_full_name") val username: String,
    @SerialName("avatar_url") val avatarUrl: String?,
    val content: String,
    val timestamp: Long,
    @SerialName("content_type") val contentType: String,
    val reactions: List<ReactionModelNetwork>
)