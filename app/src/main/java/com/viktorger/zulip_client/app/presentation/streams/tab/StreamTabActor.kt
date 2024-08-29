package com.viktorger.zulip_client.app.presentation.streams.tab

import com.viktorger.zulip_client.app.core.domain.repository.StreamsRepository
import com.viktorger.zulip_client.app.core.domain.use_case.StreamSearchUseCase
import com.viktorger.zulip_client.app.core.domain.use_case.UpdateStreamsUseCase
import com.viktorger.zulip_client.app.core.model.StreamModel
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.core.ui.model.StreamModelUi
import com.viktorger.zulip_client.app.presentation.mvi.MviActor
import com.viktorger.zulip_client.app.presentation.streams.tab.adapter.stream.StreamDelegateItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class StreamTabActor @Inject constructor(
    private val streamSearchUseCase: StreamSearchUseCase,
    private val updateStreamsUseCase: UpdateStreamsUseCase,
    private val streamsRepository: StreamsRepository
) : MviActor<
        StreamTabPartialState,
        StreamTabIntent,
        StreamTabState,
        StreamTabEffect>() {
    private var streamsLoadJob: Job? = null
    private val topicLoadJobMap: MutableMap<String, Job> = mutableMapOf()

    override fun resolve(
        intent: StreamTabIntent,
        state: StreamTabState
    ): Flow<StreamTabPartialState> =
        when (intent) {
            is StreamTabIntent.LoadStreamsWithQuery -> loadWithQuery(
                intent.query,
                intent.isSubscribed
            )

            is StreamTabIntent.UpdateData -> updateData(intent.isSubscribed)
            is StreamTabIntent.ChangeExpandedState -> changeExpandState(state, intent.streamName)
            is StreamTabIntent.NavigateToChat -> navigateToChat(intent.streamName, intent.topicName)
        }.flowOn(Dispatchers.Default)

    private fun navigateToChat(streamName: String, topicName: String): Flow<StreamTabPartialState> =
        flow {
            _effects.emit(
                StreamTabEffect.NavigateToChat(
                    streamName,
                    topicName
                )
            )
        }

    private fun updateData(
        subscribed: Boolean
    ): Flow<StreamTabPartialState> = updateStreamsUseCase(subscribed)
        .mapToPartialState()
        .catch {
            _effects.emit(StreamTabEffect.StreamsUpdateError)
            emit(StreamTabPartialState.StreamLoadError(it))
        }

    private fun loadWithQuery(
        query: String,
        isSubscribed: Boolean
    ): Flow<StreamTabPartialState> = channelFlow {
        streamsLoadJob?.cancel()
        streamsLoadJob = streamSearchUseCase(query, isSubscribed)
            .mapToPartialState()
            .onEach { send(it) }
            .launchIn(this)
    }
        .onStart { emit(StreamTabPartialState.StreamsLoading) }

    private fun changeExpandState(
        state: StreamTabState,
        streamName: String
    ): Flow<StreamTabPartialState> = channelFlow {
        val streamsUi = state.streamsUi
        if (streamsUi is LceState.Content) {
            val stream = streamsUi.data.find {
                it is StreamDelegateItem && it.content().name == streamName
            }?.content() as? StreamModelUi

            emitIfStreamNotNull(stream, streamName)
        }
    }.catch {
        emit(StreamTabPartialState.TopicsLoadError(streamName))
    }

    private suspend fun ProducerScope<StreamTabPartialState>.emitIfStreamNotNull(
        stream: StreamModelUi?,
        streamName: String
    ) {
        topicLoadJobMap[streamName]?.cancel()

        if (stream == null) {
            return
        }

        if (stream.isExpanded) {
            send(StreamTabPartialState.CollapseStream(streamName))
        } else {
            topicLoadJobMap[streamName] = streamsRepository.getAllTopics(streamName).map {
                StreamTabPartialState.TopicsLoaded(
                    streamName = streamName,
                    topics = it
                )
            }.onStart {
                send(StreamTabPartialState.StreamExpandLoading(streamName))
            }.onEach {
                send(it)
            }.catch {
                _effects.emit(StreamTabEffect.TopicUpdateError(streamName))
                send(StreamTabPartialState.TopicsLoadError(streamName))
            }.launchIn(this)
        }

    }

    private fun Flow<List<StreamModel>>.mapToPartialState(): Flow<StreamTabPartialState> =
        map { StreamTabPartialState.StreamsLoaded(it) }

}