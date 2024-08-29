package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmojiListResponse(
    @SerialName("codepoint_to_name") val codepointToName: Map<String, String>
)
