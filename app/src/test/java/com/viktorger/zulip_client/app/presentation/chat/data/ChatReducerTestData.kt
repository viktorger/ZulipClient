package com.viktorger.zulip_client.app.presentation.chat.data

import com.viktorger.zulip_client.app.core.model.ChatModel
import com.viktorger.zulip_client.app.core.model.MessagePageLoadAnswer
import com.viktorger.zulip_client.app.core.model.MessageReceivedModel
import com.viktorger.zulip_client.app.core.model.MessageSentModel
import com.viktorger.zulip_client.app.core.model.ReactionWithCounter
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.presentation.chat.ChatPartialState
import com.viktorger.zulip_client.app.presentation.chat.ChatState
import com.viktorger.zulip_client.app.presentation.chat.adapter.date.DateDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.date.DateModelUi
import com.viktorger.zulip_client.app.presentation.chat.adapter.message_shimmer.MessageShimmerDelegateItem
import com.viktorger.zulip_client.app.core.ui.model.MessagePageTag
import com.viktorger.zulip_client.app.presentation.chat.adapter.received_message.MessageReceivedDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.sent_message.MessageSentDelegateItem

class ChatReducerTestData {
    val stateWithChatUiLoading = ChatState(
        chatUi = LceState.Loading
    )

    val stateWithChatUiError = ChatState(
        chatUi = LceState.Error(Throwable())
    )

    val initialReactions = listOf(
        ReactionWithCounter(REACTION_1, 1, isSelected = false),
        ReactionWithCounter(REACTION_2, 1, isSelected = false)
    )

    val chatModel = ChatModel(
        receivedMessages = listOf(
            MessageReceivedModel(
                messageId = 0,
                userId = 4829,
                username = "Gabriela Trujillo",
                timestamp = 10000000001,
                content = "tation",
                avatarUrl = null,
                reactionsWithCounter = listOf()
            ),
            MessageReceivedModel(
                messageId = 2,
                userId = 8755,
                username = "Arnulfo Velazquez",
                timestamp = 10000000003,
                content = "regione",
                avatarUrl = null,
                reactionsWithCounter = initialReactions
            )
        ),
        sentMessages = listOf(
            MessageSentModel(
                messageId = 1,
                timestamp = 10000000002,
                content = "audire",
                reactionsWithCounter = listOf()
            ),
            MessageSentModel(
                messageId = 3,
                timestamp = 10000000004,
                content = "ea",
                reactionsWithCounter = initialReactions
            )
        )
    )

    val delegateList = listOf(
        DateDelegateItem(DateModelUi("1970", "26 апр.")),
        MessageReceivedDelegateItem(chatModel.receivedMessages[0]),
        MessageSentDelegateItem(chatModel.sentMessages[0]),
        MessageReceivedDelegateItem(chatModel.receivedMessages[1]),
        MessageSentDelegateItem(chatModel.sentMessages[1]),
    )

    private val chatUiContent = LceState.Content(
        delegateList
    )

    private val chatUiContentWithNextLoading = LceState.Content(
        delegateList + MessageShimmerDelegateItem(MessagePageTag.Next)
    )

    val stateWithChatUiContentWithNextLoading = ChatState(
        chatUiContentWithNextLoading
    )

    val stateWithChatUiContent = ChatState(
        chatUi = chatUiContent
    )

    val partialStateDataLoaded = ChatPartialState.ChatLoaded(
        chat = chatModel
    )

    val nextMessageSent = MessageSentModel(
        messageId = 7,
        timestamp = 50000000000,
        content = "facilisis",
        reactionsWithCounter = listOf()
    )
    val newMessageSentSameDate = MessageSentModel(
        messageId = 7,
        timestamp = 10000000000,
        content = "facilisis",
        reactionsWithCounter = listOf()
    )
    val partialStateNextPageLoaded = ChatPartialState.NextPageLoaded(
        MessagePageLoadAnswer(
            chat = ChatModel(
                receivedMessages = listOf(),
                sentMessages = listOf(nextMessageSent)
            ),
            limitReached = true
        )
    )
    val partialStateNextPageLoadedSameDate = ChatPartialState.NextPageLoaded(
        MessagePageLoadAnswer(
            chat = ChatModel(
                receivedMessages = listOf(),
                sentMessages = listOf(newMessageSentSameDate)
            ),
            limitReached = true
        )
    )

    private val chatUiContentWithPreviousLoading = LceState.Content(
        listOf(MessageShimmerDelegateItem(MessagePageTag.Previous)) + delegateList
    )

    val stateWithChatUiContentWithPreviousLoading = ChatState(
        chatUiContentWithPreviousLoading
    )

    val previousMessageSent = MessageSentModel(
        messageId = -1,
        timestamp = 5000000000,
        content = "facilisis",
        reactionsWithCounter = listOf()
    )
    val partialStatePreviousPageLoaded = ChatPartialState.PreviousPageLoaded(
        MessagePageLoadAnswer(
            chat = ChatModel(
                receivedMessages = listOf(),
                sentMessages = listOf(previousMessageSent)
            ),
            limitReached = true
        )
    )
    val partialStatePreviousPageLoadedSameDate = ChatPartialState.PreviousPageLoaded(
        MessagePageLoadAnswer(
            chat = ChatModel(
                receivedMessages = listOf(),
                sentMessages = listOf(newMessageSentSameDate)
            ),
            limitReached = true
        )
    )

    val dateDelegateBeforeNextMessageSent = DateDelegateItem(
        DateModelUi("1971", "2 авг.")
    )
    val chatUiContentWithNextSent = LceState.Content(
        delegateList
                + dateDelegateBeforeNextMessageSent
                + MessageSentDelegateItem(nextMessageSent)
    )
    val chatUiContentWithNextSentSameDate = LceState.Content(
        delegateList + MessageSentDelegateItem(newMessageSentSameDate)
    )

    val nextMessageReceived = MessageReceivedModel(
        messageId = 7,
        timestamp = 50000000000,
        content = "facilisis",
        reactionsWithCounter = listOf(),
        userId = 10,
        username = "Katina Coffey",
        avatarUrl = null
    )
    val newMessageReceivedSameDate = MessageReceivedModel(
        messageId = 7,
        timestamp = 10000000000,
        content = "facilisis",
        reactionsWithCounter = listOf(),
        userId = 10,
        username = "Katina Coffey",
        avatarUrl = null
    )

    val chatUiContentWithNextReceived = LceState.Content(
        delegateList + DateDelegateItem(
            DateModelUi("1971", "2 авг.")
        ) + MessageReceivedDelegateItem(nextMessageReceived)
    )
    val chatUiContentWithNextReceivedSameDate = LceState.Content(
        delegateList + MessageReceivedDelegateItem(newMessageReceivedSameDate)
    )

    companion object {
        const val REACTION_1 = "\uD83E\uDD2B"
        const val REACTION_2 = "\uD83C\uDF46"
        const val REACTION_3 = "\uD83D\uDCA6"
    }
}