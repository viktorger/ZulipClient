package com.viktorger.zulip_client.app.presentation.foreign_profile

import com.viktorger.zulip_client.app.presentation.mvi.MviIntent

sealed interface ForeignProfileIntent : MviIntent {
    data class LoadProfile(val userId: Int) : ForeignProfileIntent
    data object NavigateBack : ForeignProfileIntent
}