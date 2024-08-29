package com.viktorger.zulip_client.app.presentation.foreign_profile

import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.core.ui.model.ProfileModelUi
import com.viktorger.zulip_client.app.presentation.mvi.MviState

data class ForeignProfileState(
    val profileUi: LceState<ProfileModelUi>
) : MviState