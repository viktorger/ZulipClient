package com.viktorger.zulip_client.app.core.data.model.mapping

import com.viktorger.zulip_client.app.core.database.entities.UserEntity
import com.viktorger.zulip_client.app.core.model.UserShortcutModel
import com.viktorger.zulip_client.app.core.network.model.UserShortcutModelNetwork

fun UserShortcutModelNetwork.toExternalModel(): UserShortcutModel = UserShortcutModel(
    userId = userId,
    username = fullName,
    email = email,
    avatarUrl = avatarUrl
)

fun userShortcutModelsNetworkToEntities(
    users: List<UserShortcutModelNetwork>
): List<UserEntity> = users.map {
    it.toEntity()
}

private fun UserShortcutModelNetwork.toEntity() = UserEntity(
    userId = userId,
    username = fullName,
    email = email,
    avatarUrl = avatarUrl
)