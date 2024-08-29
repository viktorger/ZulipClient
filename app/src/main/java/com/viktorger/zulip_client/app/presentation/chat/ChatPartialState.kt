package com.viktorger.zulip_client.app.presentation.chat

import com.viktorger.zulip_client.app.core.model.ChatModel
import com.viktorger.zulip_client.app.core.model.EventSeparatedMessageModel
import com.viktorger.zulip_client.app.core.model.MessagePageLoadAnswer
import com.viktorger.zulip_client.app.presentation.mvi.MviPartialState

sealed interface ChatPartialState : MviPartialState {
    data object ChatLoading : ChatPartialState
    data class ChatLoaded(val chat: ChatModel) : ChatPartialState
    data class ChatLoadError(val error: Throwable) : ChatPartialState

    data class NextPageLoaded(
        val messagePageLoadAnswer: MessagePageLoadAnswer
    ) : ChatPartialState

    data object NextPageLoading : ChatPartialState
    data object NextPageError : ChatPartialState
    data class PreviousPageLoaded(
        val messagePageLoadAnswer: MessagePageLoadAnswer
    ) : ChatPartialState

    data object PreviousPageLoading : ChatPartialState
    data object PreviousPageError : ChatPartialState
    data class ChangeShouldScrollNextRenderToEnd(val shouldScroll: Boolean) : ChatPartialState
    data class NextEvents(val events: List<EventSeparatedMessageModel>) : ChatPartialState

}