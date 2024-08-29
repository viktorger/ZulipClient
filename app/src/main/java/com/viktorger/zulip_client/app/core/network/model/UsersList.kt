package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UsersList(
    val members: List<UserShortcutModelNetwork>
)
