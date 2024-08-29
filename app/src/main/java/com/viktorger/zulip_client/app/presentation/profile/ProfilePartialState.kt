package com.viktorger.zulip_client.app.presentation.profile

import com.viktorger.zulip_client.app.core.model.ProfileModel
import com.viktorger.zulip_client.app.presentation.mvi.MviPartialState

sealed interface ProfilePartialState : MviPartialState {
    data class ProfileLoaded(val profileModel: ProfileModel): ProfilePartialState
    data object ProfileLoading : ProfilePartialState
    data class Error(val error: Throwable): ProfilePartialState
}