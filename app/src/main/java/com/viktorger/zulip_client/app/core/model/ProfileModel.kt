package com.viktorger.zulip_client.app.core.model

data class ProfileModel (
    val userId: Int,
    val username: String,
    val status: UserStatus,
    val avatarUrl: String?
)