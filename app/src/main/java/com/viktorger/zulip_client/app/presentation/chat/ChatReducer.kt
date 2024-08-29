package com.viktorger.zulip_client.app.presentation.chat

import com.viktorger.zulip_client.app.core.common.areNotDateEqual
import com.viktorger.zulip_client.app.core.common.dateDelegateItem
import com.viktorger.zulip_client.app.core.common.toFullDateString
import com.viktorger.zulip_client.app.core.model.EventSeparatedMessageModel
import com.viktorger.zulip_client.app.core.model.MessageReceivedModel
import com.viktorger.zulip_client.app.core.model.MessageSentModel
import com.viktorger.zulip_client.app.core.model.ReactionChangeModel
import com.viktorger.zulip_client.app.core.model.ReactionWithCounter
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.core.ui.model.MessagePageLoadAnswerUi
import com.viktorger.zulip_client.app.core.ui.model.MessagePageTag
import com.viktorger.zulip_client.app.core.ui.model.mapping.toDelegateItem
import com.viktorger.zulip_client.app.core.ui.model.mapping.toDelegateItems
import com.viktorger.zulip_client.app.core.ui.model.mapping.toReactionWithCounter
import com.viktorger.zulip_client.app.core.ui.model.mapping.toUi
import com.viktorger.zulip_client.app.presentation.chat.adapter.date.DateDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.message_error.MessageErrorDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.message_shimmer.MessageShimmerDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.received_message.MessageReceivedDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.sent_message.MessageSentDelegateItem
import com.viktorger.zulip_client.app.presentation.mvi.MviReducer

class ChatReducer : MviReducer<
        ChatState,
        ChatPartialState> {
    override fun reduce(prevState: ChatState, partialState: ChatPartialState): ChatState =
        when (partialState) {
            is ChatPartialState.ChatLoaded -> {
                chatLoaded(prevState, partialState.chat.toDelegateItems())
            }

            is ChatPartialState.ChatLoadError -> handleChatLoadError(prevState, partialState)

            ChatPartialState.ChatLoading -> {
                prevState.copy(
                    chatUi = LceState.Loading
                )
            }

            ChatPartialState.NextPageLoading -> {
                pageLoading(prevState, MessagePageTag.Next)
            }

            is ChatPartialState.NextPageLoaded -> {
                nextPageLoadedNewState(prevState, partialState.messagePageLoadAnswer.toUi())
            }

            ChatPartialState.NextPageError -> {
                pageLoadingError(prevState, MessagePageTag.Next)
            }

            ChatPartialState.PreviousPageLoading -> {
                pageLoading(prevState, MessagePageTag.Previous)
            }

            is ChatPartialState.PreviousPageLoaded -> {
                prevPageLoadedNewState(prevState, partialState.messagePageLoadAnswer.toUi())
            }

            ChatPartialState.PreviousPageError -> {
                pageLoadingError(prevState, MessagePageTag.Previous)
            }


            is ChatPartialState.ChangeShouldScrollNextRenderToEnd -> prevState.copy(
                shouldScrollNextRenderToEnd = partialState.shouldScroll
            )

            is ChatPartialState.NextEvents -> {
                handleEvents(prevState, partialState.events)
            }
        }

    private fun handleEvents(
        prevState: ChatState,
        events: List<EventSeparatedMessageModel>
    ): ChatState {
        var currentState = prevState
        events.forEach { event ->
            currentState = handleSingleEvent(currentState, event)
        }
        return currentState
    }

    private fun handleSingleEvent(
        currentState: ChatState,
        event: EventSeparatedMessageModel,
    ) = when {
        event.sentMessage != null -> {
            insertNewSentMessage(currentState, event.sentMessage.toDelegateItem())
        }

        event.receivedMessage != null -> {
            insertNewReceivedMessage(currentState, event.receivedMessage.toDelegateItem())
        }

        event.reaction != null -> {
            changeReaction(currentState, event.reaction)
        }

        else -> currentState
    }

    private fun pageLoadingError(prevState: ChatState, tag: MessagePageTag): ChatState {
        if (prevState.chatUi !is LceState.Content) {
            return prevState
        }
        val delegates = prevState.chatUi.data
        val delegatesWithoutShimmer = delegatesWithoutShimmer(delegates, tag)

        val newDelegates = when (tag){
            MessagePageTag.Previous -> {
                listOf(MessageErrorDelegateItem(MessagePageTag.Previous)) + delegatesWithoutShimmer
            }
            MessagePageTag.Next -> {
                delegatesWithoutShimmer + MessageErrorDelegateItem(MessagePageTag.Next)
            }
        }

        return prevState.copy(
            chatUi = LceState.Content(newDelegates)
        )
    }

    private fun pageLoading(prevState: ChatState, tag: MessagePageTag): ChatState {
        if (prevState.chatUi !is LceState.Content) {
            return prevState
        }
        val delegates = prevState.chatUi.data
        if (delegates.any { it is MessageShimmerDelegateItem && it.content() == tag }) {
            return prevState
        }
        val delegatesWithoutError = delegatesWithoutPageError(delegates, tag)

        val newDelegates = when (tag) {
            MessagePageTag.Next -> delegatesWithoutError + MessageShimmerDelegateItem(tag)
            MessagePageTag.Previous -> listOf(MessageShimmerDelegateItem(tag)) + delegatesWithoutError
        }

        return prevState.copy(
            chatUi = LceState.Content(newDelegates)
        )
    }

    private fun handleChatLoadError(
        prevState: ChatState,
        partialState: ChatPartialState.ChatLoadError
    ) = if (prevState.chatUi is LceState.Content) {
        prevState
    } else {
        prevState.copy(
            chatUi = LceState.Error(partialState.error)
        )
    }

    private fun chatLoaded(
        prevState: ChatState,
        chat: List<DelegateItem>
    ): ChatState {
        return prevState.copy(
            chatUi = LceState.Content(chat)
        )
    }

    private fun changeReaction(
        prevState: ChatState,
        reactionChangeModel: ReactionChangeModel
    ): ChatState {
        if (prevState.chatUi !is LceState.Content) {
            return prevState
        }

        val delegates = prevState.chatUi.data
        val message = delegates.find {
            checkIfMessageIdsEqual(it, reactionChangeModel.messageId)
        }

        val editedMessage: DelegateItem? = when (message) {
            is MessageReceivedDelegateItem -> MessageReceivedDelegateItem(
                receivedMessageWithChangedReaction(message.content(), reactionChangeModel)
            )

            is MessageSentDelegateItem -> MessageSentDelegateItem(
                sentMessageWithChangedReaction(message.content(), reactionChangeModel)
            )

            else -> null
        }

        val newDelegates = if (editedMessage != null) {
            delegates.map {
                if (checkIfMessageIdsEqual(it, reactionChangeModel.messageId)) {
                    editedMessage
                } else {
                    it
                }
            }
        } else {
            delegates
        }

        return prevState.copy(
            chatUi = LceState.Content(newDelegates)
        )
    }

    private fun checkIfMessageIdsEqual(
        it: DelegateItem,
        messageId: Int
    ) = ((it is MessageReceivedDelegateItem && it.content().messageId == messageId)
            || (it is MessageSentDelegateItem && it.content().messageId == messageId))

    private fun receivedMessageWithChangedReaction(
        message: MessageReceivedModel,
        reactionChangeModel: ReactionChangeModel
    ): MessageReceivedModel = message.copy(
        reactionsWithCounter = reactionsChanged(
            message.reactionsWithCounter,
            reactionChangeModel
        )
    )


    private fun sentMessageWithChangedReaction(
        message: MessageSentModel,
        reactionChangeModel: ReactionChangeModel
    ): MessageSentModel = message.copy(
        reactionsWithCounter = reactionsChanged(
            message.reactionsWithCounter,
            reactionChangeModel
        )
    )

    private fun reactionsChanged(
        reactionsWithCounter: List<ReactionWithCounter>,
        reactionChangeModel: ReactionChangeModel
    ): List<ReactionWithCounter> {
        if (reactionsWithCounter.none { it.emoji == reactionChangeModel.emoji }) {
            return reactionsWithCounter + reactionChangeModel.toReactionWithCounter()
        }
        return reactionsWithCounter.map { reaction ->
            if (reaction.emoji == reactionChangeModel.emoji) {
                reaction.copy(
                    count = newReactionCount(reactionChangeModel.isAdded, reaction.count),
                    isSelected = (reactionChangeModel.isOwnUser && reactionChangeModel.isAdded)
                )
            } else {
                reaction
            }
        }.filter {
            it.count != 0
        }
    }

    private fun newReactionCount(
        isAdded: Boolean,
        oldCount: Int
    ) = if (isAdded) {
        oldCount + 1
    } else {
        oldCount - 1
    }


    private fun insertNewSentMessage(
        prevState: ChatState,
        messageDelegateItem: MessageSentDelegateItem
    ): ChatState {
        if (prevState.chatUi !is LceState.Content) {
            return prevState
        }
        val delegates = prevState.chatUi.data
        val shouldPlaceDate = shouldPlaceDateDelegateItem(
            delegates,
            messageDelegateItem.content().timestamp
        )

        val newDelegates = if (shouldPlaceDate) {
            delegates + dateDelegateItem(
                dateTimestamp = messageDelegateItem.content().timestamp
            ) + messageDelegateItem
        } else {
            delegates + messageDelegateItem
        }

        return prevState.copy(
            chatUi = LceState.Content(newDelegates),
            shouldScrollNextRenderToEnd = true
        )
    }

    private fun insertNewReceivedMessage(
        prevState: ChatState,
        messageDelegateItem: MessageReceivedDelegateItem
    ): ChatState {
        if (prevState.chatUi !is LceState.Content) {
            return prevState
        }
        val delegates = prevState.chatUi.data
        val shouldPlaceDate = shouldPlaceDateDelegateItem(
            delegates = delegates,
            messagesTimeStamp = messageDelegateItem.content().timestamp
        )

        val newDelegates = if (shouldPlaceDate) {
            delegates + dateDelegateItem(
                dateTimestamp = messageDelegateItem.content().timestamp
            ) + messageDelegateItem
        } else {
            delegates + messageDelegateItem
        }

        return prevState.copy(
            chatUi = LceState.Content(newDelegates)
        )
    }

    private fun shouldPlaceDateDelegateItem(
        delegates: List<DelegateItem>,
        messagesTimeStamp: Long
    ): Boolean {
        val lastMessage = delegates.findLast {
            it is MessageReceivedDelegateItem || it is MessageSentDelegateItem
        }
        val lastDateTimestamp = when (lastMessage) {
            is MessageReceivedDelegateItem -> lastMessage.content().timestamp
            is MessageSentDelegateItem -> lastMessage.content().timestamp
            else -> 0
        }
        return areNotDateEqual(lastDateTimestamp, messagesTimeStamp)
    }

    private fun shouldRemoveDateDelegateItem(
        firstDelegates: List<DelegateItem>,
        secondDelegates: List<DelegateItem>
    ): Boolean {
        val lastMessage = firstDelegates.findLast {
            it is MessageReceivedDelegateItem || it is MessageSentDelegateItem
        }
        val lastDateTimestamp = when (lastMessage) {
            is MessageReceivedDelegateItem -> lastMessage.content().timestamp
            is MessageSentDelegateItem -> lastMessage.content().timestamp
            else -> 0
        }

        val firstDateItem = secondDelegates.find {
            it is DateDelegateItem
        } as? DateDelegateItem
        val firstDate = firstDateItem?.content()?.getFullDate() ?: ""
        return lastDateTimestamp.toFullDateString() == firstDate
    }


    private fun prevPageLoadedNewState(
        prevState: ChatState,
        messagePageLoadAnswerUi: MessagePageLoadAnswerUi
    ): ChatState {
        if (prevState.chatUi !is LceState.Content) {
            return prevState
        }

        val delegates = delegatesWithoutShimmer(prevState.chatUi.data, MessagePageTag.Previous)
        val shouldRemoveDate = shouldRemoveDateDelegateItem(
            firstDelegates = messagePageLoadAnswerUi.chat,
            secondDelegates = delegates
        )
        val newDelegates = if (shouldRemoveDate) {
            messagePageLoadAnswerUi.chat + delegates.tryRemoveFirst()
        } else {
            messagePageLoadAnswerUi.chat + delegates
        }

        return prevState.copy(
            chatUi = LceState.Content(newDelegates),
            previousPageLimitReached = messagePageLoadAnswerUi.limitReached
        )
    }

    private fun delegatesWithoutShimmer(
        delegates: List<DelegateItem>,
        tag: MessagePageTag
    ) = delegates.filter {
        !(it is MessageShimmerDelegateItem && it.content() == tag)
    }

    private fun delegatesWithoutPageError(
        delegates: List<DelegateItem>,
        tag: MessagePageTag
    ) = delegates.filter {
        !(it is MessageErrorDelegateItem && it.content() == tag)
    }

    private fun nextPageLoadedNewState(
        prevState: ChatState,
        messagePageLoadAnswerUi: MessagePageLoadAnswerUi
    ): ChatState {
        if (prevState.chatUi !is LceState.Content) {
            return prevState
        }

        val delegates = delegatesWithoutShimmer(prevState.chatUi.data, MessagePageTag.Next)
        val shouldRemoveDate = shouldRemoveDateDelegateItem(
            firstDelegates = delegates,
            secondDelegates = messagePageLoadAnswerUi.chat
        )
        val newDelegates = if (shouldRemoveDate) {
            delegates + messagePageLoadAnswerUi.chat.tryRemoveFirst()
        } else {
            delegates + messagePageLoadAnswerUi.chat
        }

        return prevState.copy(
            chatUi = LceState.Content(newDelegates),
            nextPageLimitReached = messagePageLoadAnswerUi.limitReached
        )
    }

    private fun <T> List<T>.tryRemoveFirst(): List<T> = if (isNotEmpty()) {
        this.subList(1, size)
    } else {
        this
    }

}