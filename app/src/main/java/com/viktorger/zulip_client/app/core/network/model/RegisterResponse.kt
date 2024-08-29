package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    @SerialName("queue_id") val queueId: String,
    @SerialName("last_event_id") val lastEventId: Int
)
