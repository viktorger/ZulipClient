package com.viktorger.zulip_client.app.presentation.chat

import com.viktorger.zulip_client.app.presentation.mvi.MviEffect

sealed interface ChatEffect : MviEffect {
    data class ChatLoadError(val throwable: Throwable) : ChatEffect
    data object SendMessageError : ChatEffect
    data object ChangeReactionError : ChatEffect
    data object EventError : ChatEffect
    data object PageLoadError : ChatEffect
    data object NavigateBack : ChatEffect

}