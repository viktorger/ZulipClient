package com.viktorger.zulip_client.app.presentation.foreign_profile

import com.viktorger.zulip_client.app.core.model.ProfileModel
import com.viktorger.zulip_client.app.presentation.mvi.MviPartialState

sealed interface ForeignProfilePartialState : MviPartialState {
    data class ProfileLoaded(val profileModel: ProfileModel): ForeignProfilePartialState
    data object ProfileLoading : ForeignProfilePartialState
    data class Error(val error: Throwable): ForeignProfilePartialState
}