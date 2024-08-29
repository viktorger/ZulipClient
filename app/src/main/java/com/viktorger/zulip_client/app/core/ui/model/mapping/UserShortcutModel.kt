package com.viktorger.zulip_client.app.core.ui.model.mapping

import com.viktorger.zulip_client.app.core.model.UserShortcutModel
import com.viktorger.zulip_client.app.core.ui.model.UserShortcutModelUi
import com.viktorger.zulip_client.app.core.ui.model.UserStatusUi

internal fun userShortcutModelsToUi(users: List<UserShortcutModel>) = users.map { it.toUi() }
private fun UserShortcutModel.toUi() = UserShortcutModelUi(
    userId = userId,
    username = username,
    email = email,
    avatarUrl = avatarUrl,
    status = UserStatusUi.Unknown
)