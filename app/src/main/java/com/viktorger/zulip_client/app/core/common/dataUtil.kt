package com.viktorger.zulip_client.app.core.common

import com.viktorger.zulip_client.app.core.model.UserStatus

internal fun buildSqlLikeQuery(query: String): String = "%$query%"

internal fun getUserStatus(status: String): UserStatus = when (status) {
    "active" -> UserStatus.Active

    "idle" -> UserStatus.Idle

    "offline" -> UserStatus.Offline

    else -> UserStatus.Unknown
}