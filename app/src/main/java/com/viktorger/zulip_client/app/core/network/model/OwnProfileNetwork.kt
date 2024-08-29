package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnProfileNetwork(
    @SerialName("user_id") val userId: Int,
    @SerialName("full_name") val fullName: String,
    @SerialName("avatar_url") val avatarUrl: String?
)
