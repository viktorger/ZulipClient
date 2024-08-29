package com.viktorger.zulip_client.app.presentation.streams.tab

import com.viktorger.zulip_client.app.presentation.mvi.MviIntent

sealed interface StreamTabIntent : MviIntent {
    data class LoadStreamsWithQuery(val query: String, val isSubscribed: Boolean) : StreamTabIntent
    data class UpdateData(val isSubscribed: Boolean) : StreamTabIntent
    data class ChangeExpandedState(val streamName: String): StreamTabIntent
    data class NavigateToChat(val streamName: String, val topicName: String) : StreamTabIntent
}