package com.viktorger.zulip_client.app.core.model

sealed class UserStatus {
    data object Active : UserStatus()
    data object Idle : UserStatus()
    data object Offline : UserStatus()
    data object Unknown : UserStatus()
}