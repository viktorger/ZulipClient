package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReactionModelNetwork(
    @SerialName("emoji_code") val emojiCode: String,
    @SerialName("user_id") val userId: Int,
    @SerialName("reaction_type") val reactionType: String
)