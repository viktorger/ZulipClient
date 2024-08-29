package com.viktorger.zulip_client.app.presentation.foreign_profile

import com.viktorger.zulip_client.app.presentation.mvi.MviEffect

sealed interface ForeignProfileEffect : MviEffect {
    data class ShowError(val throwable: Throwable) : ForeignProfileEffect
    data object NavigateBack : ForeignProfileEffect
}