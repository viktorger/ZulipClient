package com.viktorger.zulip_client.app.core.model

data class UserShortcutModel(
    val userId: Int,
    val username: String,
    val email: String,
    val avatarUrl: String?
)
