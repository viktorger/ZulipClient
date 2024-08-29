package com.viktorger.zulip_client.app.core.data.model.mapping

import com.viktorger.zulip_client.app.core.database.entities.UserEntity
import com.viktorger.zulip_client.app.core.model.UserShortcutModel

fun userEntitiesToExternal(
    users: List<UserEntity>
): List<UserShortcutModel> = users.map {
    it.toExternalModel()
}

private fun UserEntity.toExternalModel() = UserShortcutModel(
    userId = userId,
    username = username,
    email = email,
    avatarUrl = avatarUrl
)