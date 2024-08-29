package com.viktorger.zulip_client.app.core.ui.model

data class UserShortcutModelUi(
    val userId: Int,
    val username: String,
    val email: String,
    val avatarUrl: String?,
    val status: UserStatusUi
)
