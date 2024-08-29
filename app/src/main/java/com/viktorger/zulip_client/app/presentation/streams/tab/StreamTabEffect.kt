package com.viktorger.zulip_client.app.presentation.streams.tab

import com.viktorger.zulip_client.app.presentation.mvi.MviEffect

sealed interface StreamTabEffect : MviEffect {
    data object StreamsUpdateError : StreamTabEffect
    data class TopicUpdateError(val streamName: String) : StreamTabEffect
    data class NavigateToChat(val streamName: String, val topicName: String) : StreamTabEffect
}