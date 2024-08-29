package com.viktorger.zulip_client.app.core.domain.repository

import com.viktorger.zulip_client.app.core.model.StreamModel
import com.viktorger.zulip_client.app.core.model.TopicModel
import kotlinx.coroutines.flow.Flow

interface StreamsRepository {
    fun getAllStreams(query: String): Flow<List<StreamModel>>
    fun getSubscribedStreams(query: String): Flow<List<StreamModel>>
    fun getAllTopics(streamName: String): Flow<List<TopicModel>>
    fun updateAllStreams(): Flow<List<StreamModel>>
    fun updateSubscribedStreams(): Flow<List<StreamModel>>
}