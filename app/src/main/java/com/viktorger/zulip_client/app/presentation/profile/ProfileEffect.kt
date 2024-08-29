package com.viktorger.zulip_client.app.presentation.profile

import com.viktorger.zulip_client.app.presentation.mvi.MviEffect

sealed interface ProfileEffect : MviEffect {
    data class ShowError(val throwable: Throwable) : ProfileEffect
}