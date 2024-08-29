package com.viktorger.zulip_client.app.presentation.chat

import com.viktorger.zulip_client.app.core.common.NO_MESSAGES
import com.viktorger.zulip_client.app.core.common.runCatchingNonCancellation
import com.viktorger.zulip_client.app.core.domain.repository.MessagesRepository
import com.viktorger.zulip_client.app.core.domain.use_case.GetNextEventsUseCase
import com.viktorger.zulip_client.app.core.domain.use_case.GetSavedChatUseCase
import com.viktorger.zulip_client.app.core.domain.use_case.LoadPageAfterIdUseCase
import com.viktorger.zulip_client.app.core.domain.use_case.LoadPageBeforeIdUseCase
import com.viktorger.zulip_client.app.core.model.ChatModel
import com.viktorger.zulip_client.app.core.model.InitialChatRequest
import com.viktorger.zulip_client.app.core.model.LoadMessagePageRequest
import com.viktorger.zulip_client.app.core.model.SendMessageParams
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.core.ui.model.MessagePageTag
import com.viktorger.zulip_client.app.presentation.chat.adapter.received_message.MessageReceivedDelegateItem
import com.viktorger.zulip_client.app.presentation.chat.adapter.sent_message.MessageSentDelegateItem
import com.viktorger.zulip_client.app.presentation.mvi.MviActor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatActor @Inject constructor(
    private val messagesRepository: MessagesRepository,
    private val getSavedChatUseCase: GetSavedChatUseCase,
    private val loadPageAfterIdUseCase: LoadPageAfterIdUseCase,
    private val loadPageBeforeIdUseCase: LoadPageBeforeIdUseCase,
    private val getNextEventsUseCase: GetNextEventsUseCase
) : MviActor<
        ChatPartialState,
        ChatIntent,
        ChatState,
        ChatEffect>() {
    override fun resolve(intent: ChatIntent, state: ChatState): Flow<ChatPartialState> {
        return when (intent) {
            is ChatIntent.LoadSavedChat -> loadSavedChat(
                InitialChatRequest(
                    streamName = intent.streamName,
                    topicName = intent.topicName,
                )
            )

            is ChatIntent.LoadNextPage -> loadPageAfterId(
                LoadMessagePageRequest(
                    streamName = intent.streamName,
                    topicName = intent.topicName,
                    messageId = getLastMessageId(state.chatUi)
                )
            )

            is ChatIntent.LoadPreviousPage -> loadPageBeforeId(
                LoadMessagePageRequest(
                    streamName = intent.streamName,
                    topicName = intent.topicName,
                    messageId = getFirstMessageId(state.chatUi)
                )
            )

            is ChatIntent.SendMessage -> {
                sendMessage(
                    intent.streamName,
                    intent.topicName,
                    intent.messageContent
                )
            }

            is ChatIntent.AddReaction -> addReaction(
                state,
                intent.messageId,
                intent.emoji
            )

            is ChatIntent.UpdateReaction -> updateReaction(
                intent.messageId,
                intent.emoji,
                intent.isSelected
            )

            is ChatIntent.CatchEvents -> catchEvents(
                intent.streamName,
                intent.topicName
            )

            ChatIntent.SetShouldScrollNextRenderToEndFalse -> setShouldScrollNextRenderToEndFalse()
            is ChatIntent.ReloadPage -> reloadPage(
                intent.streamName,
                intent.topicName,
                intent.messagePageTag,
                state
            )

            ChatIntent.NavigateBack -> navigateBack()
        }.flowOn(Dispatchers.Default)
    }

    private fun navigateBack(): Flow<ChatPartialState> = flow {
        _effects.emit(ChatEffect.NavigateBack)
    }

    private fun reloadPage(
        streamName: String,
        topicName: String,
        messagePageTag: MessagePageTag,
        state: ChatState
    ): Flow<ChatPartialState> = when (messagePageTag) {
        MessagePageTag.Previous -> loadPageBeforeId(
            LoadMessagePageRequest(
                streamName = streamName,
                topicName = topicName,
                messageId = getFirstMessageId(state.chatUi)
            )
        )

        MessagePageTag.Next -> loadPageAfterId(
            LoadMessagePageRequest(
                streamName = streamName,
                topicName = topicName,
                messageId = getLastMessageId(state.chatUi)
            )
        )
    }

    private fun setShouldScrollNextRenderToEndFalse(): Flow<ChatPartialState> = flow {
        emit(ChatPartialState.ChangeShouldScrollNextRenderToEnd(false))
    }

    private fun catchEvents(streamName: String, topicName: String): Flow<ChatPartialState> =
        flow<ChatPartialState> {
            val registrationAnswer = messagesRepository.registerEventQueue(streamName, topicName)
            var lastEventId = registrationAnswer.lastEventId

            while (currentCoroutineContext().isActive) {
                val events = getNextEventsUseCase(registrationAnswer.queueId, lastEventId)

                emit(ChatPartialState.NextEvents(events))
                lastEventId = events.last().eventId
            }
            messagesRepository.deleteEventQueue(registrationAnswer.queueId)
        }
            .catch {
                _effects.emit(ChatEffect.EventError)
            }

    private fun getFirstMessageId(chatUi: LceState<List<DelegateItem>>): Int {
        if (chatUi !is LceState.Content || chatUi.data.isEmpty()) {
            return NO_MESSAGES
        }

        val message = chatUi.data.find {
            it is MessageSentDelegateItem || it is MessageReceivedDelegateItem
        }

        return when (message) {
            is MessageSentDelegateItem -> message.content().messageId
            is MessageReceivedDelegateItem -> message.content().messageId
            else -> NO_MESSAGES
        }
    }

    private fun getLastMessageId(chatUi: LceState<List<DelegateItem>>): Int {
        if (chatUi !is LceState.Content || chatUi.data.isEmpty()) {
            return NO_MESSAGES
        }

        val message = chatUi.data.findLast {
            it is MessageSentDelegateItem || it is MessageReceivedDelegateItem
        }

        return when (message) {
            is MessageSentDelegateItem -> message.content().messageId
            is MessageReceivedDelegateItem -> message.content().messageId
            else -> NO_MESSAGES
        }
    }

    private var loadNextPageJob: Job? = null
    private var loadPrevPageJob: Job? = null

    private fun loadPageAfterId(
        request: LoadMessagePageRequest
    ): Flow<ChatPartialState> = channelFlow {
        loadNextPageJob?.cancel()
        loadNextPageJob = launch {
            runCatchingNonCancellation {
                loadPageAfterIdUseCase(request)
            }.fold(
                onSuccess = { send(ChatPartialState.NextPageLoaded(it)) },
                onFailure = {
                    send(ChatPartialState.NextPageError)
                    _effects.emit(ChatEffect.PageLoadError)
                }
            )
        }
    }.onStart {
        emit(ChatPartialState.NextPageLoading)
    }

    private fun loadPageBeforeId(
        request: LoadMessagePageRequest
    ): Flow<ChatPartialState> = channelFlow {
        loadPrevPageJob?.cancel()
        loadPrevPageJob = launch {
            runCatchingNonCancellation {
                loadPageBeforeIdUseCase(request)
            }.fold(
                onSuccess = { send(ChatPartialState.PreviousPageLoaded(it)) },
                onFailure = {
                    _effects.emit(ChatEffect.PageLoadError)
                    send(ChatPartialState.PreviousPageError)
                }
            )
        }
    }.onStart {
        emit(ChatPartialState.PreviousPageLoading)
    }

    private fun loadSavedChat(
        initialChatRequest: InitialChatRequest
    ): Flow<ChatPartialState> = getSavedChatUseCase(initialChatRequest)
        .mapToPartialState()
        .catch {
            _effects.emit(ChatEffect.ChatLoadError(it))
            emit(ChatPartialState.ChatLoadError(it))
        }
        .onStart { emit(ChatPartialState.ChatLoading) }
        .onCompletion { throwable ->
            if (throwable == null) {
                catchEvents(initialChatRequest.streamName, initialChatRequest.topicName).collect {
                    emit(it)
                }
            }
        }

    private fun updateReaction(
        messageId: Int,
        emoji: String,
        isSelected: Boolean
    ): Flow<ChatPartialState> = flow {
        runCatchingNonCancellation {
            if (isSelected) {
                messagesRepository.removeReaction(messageId, emoji)
            } else {
                messagesRepository.addReaction(messageId, emoji)
            }
        }.onFailure {
            _effects.emit(ChatEffect.ChangeReactionError)
        }
    }

    private fun addReaction(
        state: ChatState,
        messageId: Int,
        emoji: String
    ): Flow<ChatPartialState> = flow {
        if (state.chatUi is LceState.Content) {
            val existsInReactionList = existsInMessagesReactions(state.chatUi, messageId, emoji)

            if (!existsInReactionList) {
                runCatchingNonCancellation {
                    messagesRepository.addReaction(
                        messageId, emoji
                    )
                }.onFailure {
                    _effects.emit(ChatEffect.ChangeReactionError)
                }
            }
        }

    }

    private fun existsInMessagesReactions(
        chatUi: LceState.Content<List<DelegateItem>>,
        messageId: Int,
        emoji: String
    ): Boolean {
        val delegateItem = chatUi.data.find {
            it is MessageSentDelegateItem && it.content().messageId == messageId
                    || it is MessageReceivedDelegateItem && it.content().messageId == messageId
        }
        val existsInReactionList = when (delegateItem) {
            is MessageSentDelegateItem -> delegateItem.content().reactionsWithCounter.any {
                it.emoji == emoji
            }

            is MessageReceivedDelegateItem -> delegateItem.content().reactionsWithCounter.any {
                it.emoji == emoji
            }

            else -> false
        }
        return existsInReactionList
    }

    private fun sendMessage(
        streamName: String,
        topicName: String,
        messageContent: String
    ): Flow<ChatPartialState> = flow {
        runCatchingNonCancellation {
            messagesRepository.sendChatMessage(
                SendMessageParams(
                    streamName = streamName,
                    topicName = topicName,
                    content = messageContent
                )
            )
        }.onFailure { _effects.emit(ChatEffect.SendMessageError) }
    }

    private fun Flow<ChatModel>.mapToPartialState(): Flow<ChatPartialState> =
        map { ChatPartialState.ChatLoaded(it) }

}