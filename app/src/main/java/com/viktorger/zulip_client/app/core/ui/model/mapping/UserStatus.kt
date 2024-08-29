package com.viktorger.zulip_client.app.core.ui.model.mapping

import com.viktorger.zulip_client.app.core.model.UserStatus
import com.viktorger.zulip_client.app.core.ui.model.UserStatusUi

internal fun UserStatus.toUi(): UserStatusUi = when (this) {
    UserStatus.Active -> UserStatusUi.Active
    UserStatus.Idle -> UserStatusUi.Idle
    UserStatus.Offline -> UserStatusUi.Offline
    UserStatus.Unknown -> UserStatusUi.Unknown
}