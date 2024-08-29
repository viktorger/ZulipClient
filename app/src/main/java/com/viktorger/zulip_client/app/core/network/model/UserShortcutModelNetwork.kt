package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserShortcutModelNetwork(
    @SerialName("user_id") val userId: Int,
    @SerialName("full_name") val fullName: String,
    val email: String,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("is_bot") val isBot: Boolean,
    @SerialName("is_active") val isActive: Boolean
)
