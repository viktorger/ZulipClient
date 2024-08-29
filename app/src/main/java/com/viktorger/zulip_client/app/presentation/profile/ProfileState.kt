package com.viktorger.zulip_client.app.presentation.profile

import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.core.ui.model.ProfileModelUi
import com.viktorger.zulip_client.app.presentation.mvi.MviState

data class ProfileState(
    val profileUi: LceState<ProfileModelUi>
) : MviState