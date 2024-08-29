package com.viktorger.zulip_client.app.presentation.profile

import com.viktorger.zulip_client.app.presentation.mvi.MviIntent

sealed interface ProfileIntent : MviIntent {
    data object Init : ProfileIntent
}