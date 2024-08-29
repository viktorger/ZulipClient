package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StreamIdResponse(
    @SerialName("stream_id") val streamId: Int
)
