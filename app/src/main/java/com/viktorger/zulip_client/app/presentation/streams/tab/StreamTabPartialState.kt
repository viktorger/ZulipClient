package com.viktorger.zulip_client.app.presentation.streams.tab

import com.viktorger.zulip_client.app.core.model.StreamModel
import com.viktorger.zulip_client.app.core.model.TopicModel
import com.viktorger.zulip_client.app.presentation.mvi.MviPartialState

sealed interface StreamTabPartialState : MviPartialState {
    data class StreamsLoaded(val streams: List<StreamModel>) : StreamTabPartialState

    data class StreamExpandLoading(val streamName: String) : StreamTabPartialState
    data class TopicsLoaded(
        val streamName: String, val topics: List<TopicModel>
    ) : StreamTabPartialState

    data class TopicsLoadError(val streamName: String) : StreamTabPartialState
    data class CollapseStream(
        val streamName: String
    ) : StreamTabPartialState

    data object StreamsLoading : StreamTabPartialState
    data class StreamLoadError(val error: Throwable) : StreamTabPartialState
}