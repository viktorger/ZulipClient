package com.viktorger.zulip_client.app.core.ui.model

data class ProfileModelUi(
    val userId: Int,
    val username: String,
    val status: UserStatusUi,
    val avatarUrl: String?
)
