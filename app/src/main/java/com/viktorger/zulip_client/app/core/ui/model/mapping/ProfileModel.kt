package com.viktorger.zulip_client.app.core.ui.model.mapping

import com.viktorger.zulip_client.app.core.model.ProfileModel
import com.viktorger.zulip_client.app.core.ui.model.ProfileModelUi

fun ProfileModel.toUi(): ProfileModelUi = ProfileModelUi(
    userId = userId,
    username = username,
    status = status.toUi(),
    avatarUrl = avatarUrl
)