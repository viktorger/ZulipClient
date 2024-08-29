package com.viktorger.zulip_client.app.presentation.streams.tab

import com.viktorger.zulip_client.app.core.common.streamModelsToDelegateItems
import com.viktorger.zulip_client.app.core.common.topicModelsToDelegateItems
import com.viktorger.zulip_client.app.core.ui.adapters.DelegateItem
import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.core.ui.model.StreamModelUi
import com.viktorger.zulip_client.app.presentation.mvi.MviReducer
import com.viktorger.zulip_client.app.presentation.streams.tab.adapter.stream.StreamDelegateItem
import com.viktorger.zulip_client.app.presentation.streams.tab.adapter.topic.TopicDelegateItem
import com.viktorger.zulip_client.app.presentation.streams.tab.adapter.topic_shimmer.TopicShimmerDelegateItem

class StreamTabReducer : MviReducer<
        StreamTabState,
        StreamTabPartialState> {
    override fun reduce(
        prevState: StreamTabState,
        partialState: StreamTabPartialState
    ): StreamTabState =
        when (partialState) {
            is StreamTabPartialState.StreamsLoaded -> streamsLoaded(
                prevState,
                streamModelsToDelegateItems(partialState.streams)
            )

            is StreamTabPartialState.StreamLoadError -> handleStreamLoadError(
                prevState,
                partialState
            )


            StreamTabPartialState.StreamsLoading -> handleStreamsLoading(prevState)

            is StreamTabPartialState.CollapseStream -> collapseStream(
                prevState,
                partialState.streamName
            )

            is StreamTabPartialState.TopicsLoaded -> topicsLoaded(
                prevState,
                partialState.streamName,
                topicModelsToDelegateItems(partialState.topics)
            )

            is StreamTabPartialState.TopicsLoadError -> removeTopicShimmerAfterStream(
                prevState,
                partialState.streamName
            )

            is StreamTabPartialState.StreamExpandLoading -> streamExpandLoading(
                prevState,
                partialState.streamName
            )
        }

    private fun handleStreamsLoading(prevState: StreamTabState): StreamTabState {
        return prevState.copy(
            streamsUi = if (prevState.streamsUi is LceState.Content) {
                prevState.streamsUi
            } else {
                LceState.Loading
            }
        )
    }

    private fun streamsLoaded(
        prevState: StreamTabState,
        streams: List<DelegateItem>
    ): StreamTabState {
        if (prevState.streamsUi !is LceState.Content) {
            return prevState.copy(
                streamsUi = LceState.Content(streams)
            )
        }

        val delegates = prevState.streamsUi.data

        val newDelegates = mutableListOf<DelegateItem>()
        streams.forEach { newStream ->
            if (newStream !is StreamDelegateItem) {
                return@forEach
            }

            val delegatesWithStreamName = delegates.filter {
                (it is StreamDelegateItem && it.content().name == newStream.content().name)
                        || (it is TopicDelegateItem && it.content().streamName == newStream.content().name)
            }

            if (delegatesWithStreamName.isNotEmpty()) {
                newDelegates.addAll(delegatesWithStreamName)
            } else {
                newDelegates.add(newStream)
            }

        }
        return prevState.copy(
            streamsUi = LceState.Content(newDelegates)
        )
    }

    private fun handleStreamLoadError(
        prevState: StreamTabState,
        partialState: StreamTabPartialState.StreamLoadError
    ): StreamTabState = prevState.copy(
        streamsUi = if (prevState.streamsUi !is LceState.Content) {
            LceState.Error(partialState.error)
        } else {
            prevState.streamsUi
        }
    )


    private fun removeTopicShimmerAfterStream(
        prevState: StreamTabState,
        streamName: String
    ): StreamTabState {
        if (prevState.streamsUi !is LceState.Content) {
            return prevState
        }

        val delegates = prevState.streamsUi.data
        val newDelegates: List<DelegateItem> = delegates.filter {
            !(it is TopicShimmerDelegateItem && it.content() == streamName)
        }

        return prevState.copy(
            streamsUi = LceState.Content(newDelegates)
        )
    }

    private fun streamExpandLoading(
        prevState: StreamTabState,
        streamName: String
    ): StreamTabState {
        if (prevState.streamsUi !is LceState.Content) {
            return prevState
        }

        val delegates = prevState.streamsUi.data.map {
            if (it is StreamDelegateItem && it.content().name == streamName) {
                streamDelegateItem(it.content(), true)
            } else {
                it
            }
        }
        val streamIndex = delegates.indexOfFirst {
            it is StreamDelegateItem && it.content().name == streamName
        }

        val newDelegates: List<DelegateItem> = if (streamIndex != -1) {
            delegates.subList(
                fromIndex = 0,
                toIndex = streamIndex + 1
            ) + TopicShimmerDelegateItem(streamName) + delegates.subList(
                fromIndex = streamIndex + 1,
                toIndex = delegates.size
            )
        } else {
            delegates
        }

        return prevState.copy(
            streamsUi = LceState.Content(newDelegates)
        )
    }

    private fun topicsLoaded(
        prevState: StreamTabState,
        streamName: String,
        topics: List<DelegateItem>
    ): StreamTabState {
        if (prevState.streamsUi !is LceState.Content) {
            return prevState
        }

        val delegates = prevState.streamsUi.data.filter {
            !(it is TopicDelegateItem && it.content().streamName == streamName)
                    && !(it is TopicShimmerDelegateItem && it.content() == streamName)
        }
        val streamIndex = delegates.indexOfFirst {
            it is StreamDelegateItem && it.content().name == streamName && it.content().isExpanded
        }

        val newDelegates: List<DelegateItem> = if (streamIndex != -1) {
            delegates.subList(
                fromIndex = 0,
                toIndex = streamIndex + 1
            ) + topics + delegates.subList(
                fromIndex = streamIndex + 1,
                toIndex = delegates.size
            )
        } else {
            delegates
        }

        return prevState.copy(
            streamsUi = LceState.Content(newDelegates)
        )
    }

    private fun collapseStream(prevState: StreamTabState, streamName: String): StreamTabState {
        if (prevState.streamsUi !is LceState.Content) {
            return prevState
        }

        val delegates = prevState.streamsUi.data
        val newDelegates: List<DelegateItem> = delegates.filter {
            !(it is TopicDelegateItem && it.content().streamName == streamName)
                    && !(it is TopicShimmerDelegateItem && it.content() == streamName)
        }.map {
            if (it is StreamDelegateItem
                && it.content().name == streamName
            ) {
                streamDelegateItem(it.content(), false)
            } else {
                it
            }
        }

        return prevState.copy(
            streamsUi = LceState.Content(newDelegates)
        )
    }

    private fun streamDelegateItem(streamModelUi: StreamModelUi, isExpanded: Boolean) =
        StreamDelegateItem(
            streamModelUi.copy(
                isExpanded = isExpanded
            )
        )
}
