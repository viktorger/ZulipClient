package com.viktorger.zulip_client.app.presentation.chat

import com.viktorger.zulip_client.app.core.ui.model.MessagePageTag
import com.viktorger.zulip_client.app.presentation.mvi.MviIntent

sealed interface ChatIntent : MviIntent {
    data class LoadSavedChat(val streamName: String, val topicName: String) : ChatIntent
    data class LoadNextPage(val streamName: String, val topicName: String): ChatIntent
    data class LoadPreviousPage(val streamName: String, val topicName: String): ChatIntent
    data object NavigateBack : ChatIntent
    data class ReloadPage(
        val streamName: String,
        val topicName: String,
        val messagePageTag: MessagePageTag
    ): ChatIntent
    data class CatchEvents(val streamName: String, val topicName: String): ChatIntent

    data class SendMessage(
        val streamName: String,
        val topicName: String,
        val messageContent: String
    ) : ChatIntent

    data class AddReaction(
        val messageId: Int,
        val emoji: String
    ) : ChatIntent

    data class UpdateReaction(
        val messageId: Int,
        val emoji: String,
        val isSelected: Boolean
    ) : ChatIntent

    data object SetShouldScrollNextRenderToEndFalse : ChatIntent


}