package com.viktorger.zulip_client.app.core.data.repository

import com.viktorger.zulip_client.app.core.common.buildSqlLikeQuery
import com.viktorger.zulip_client.app.core.data.model.mapping.streamEntitiesToExternal
import com.viktorger.zulip_client.app.core.data.model.mapping.streamModelsNetworkToEntities
import com.viktorger.zulip_client.app.core.data.model.mapping.streamModelsNetworkToExternal
import com.viktorger.zulip_client.app.core.data.model.mapping.topicEntitiesToExternal
import com.viktorger.zulip_client.app.core.data.model.mapping.topicModelsToEntities
import com.viktorger.zulip_client.app.core.database.dao.StreamDao
import com.viktorger.zulip_client.app.core.database.dao.TopicDao
import com.viktorger.zulip_client.app.core.domain.repository.StreamsRepository
import com.viktorger.zulip_client.app.core.model.StreamModel
import com.viktorger.zulip_client.app.core.model.TopicModel
import com.viktorger.zulip_client.app.core.network.ZulipApi
import com.viktorger.zulip_client.app.core.network.model.StreamModelNetwork
import com.viktorger.zulip_client.app.core.network.model.TopicModelNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StreamsRepositoryImpl @Inject constructor(
    private val streamDao: StreamDao,
    private val topicDao: TopicDao,
    private val zulipApi: ZulipApi
) : StreamsRepository {
    override fun getAllStreams(query: String): Flow<List<StreamModel>> = flow {
        val cached = streamDao.getAllStreams(buildSqlLikeQuery(query))
        if (!(cached.isEmpty() && query.isEmpty())) {
            emit(streamEntitiesToExternal(cached))
        }
    }

    override fun getSubscribedStreams(query: String): Flow<List<StreamModel>> = flow {
        val cached = streamDao.getSubscribedStreams(buildSqlLikeQuery(query))
        if (!(cached.isEmpty() && query.isEmpty())) {
            emit(streamEntitiesToExternal(cached))
        }
    }

    override fun getAllTopics(
        streamName: String
    ): Flow<List<TopicModel>> = flow {
        val cached = topicDao.getTopicsByStreamName(streamName)
        if (cached.isNotEmpty()) {
            emit(topicEntitiesToExternal(cached))
        }

        val streamId = zulipApi.getStreamId(streamName).streamId
        val downloadedTopics = loadTopicsInStream(streamName, streamId).sortedBy { it.topicName }
        emit(downloadedTopics)

        updateCachedTopicsInStream(streamName, downloadedTopics)
    }

    private fun updateCachedTopicsInStream(streamName: String, topics: List<TopicModel>) {
        val topicEntities = topicModelsToEntities(topics)
        topicDao.updateTopicsInStream(streamName, topicEntities)
    }

    override fun updateAllStreams(): Flow<List<StreamModel>> = flow {
        val streams = zulipApi.getAllStreams().streams
        emit(streamModelsNetworkToExternal(streams).sortedBy { it.name })

        updateCachedStreams(streams, false)
    }

    override fun updateSubscribedStreams(): Flow<List<StreamModel>> = flow {
        val streams = zulipApi.getSubscribedStreams().subscriptions
        emit(streamModelsNetworkToExternal(streams).sortedBy { it.name })

        updateCachedStreams(streams, true)
    }

    private fun updateCachedStreams(
        streams: List<StreamModelNetwork>,
        isSubscribed: Boolean
    ) {
        val streamEntities = streamModelsNetworkToEntities(streams, isSubscribed)

        if (isSubscribed) {
            streamDao.rewriteSubscribedStreams(streamEntities)
        } else {
            streamDao.rewriteAllStreams(streamEntities)
        }
    }

    private suspend fun loadTopicsInStream(
        streamName: String,
        streamId: Int
    ): List<TopicModel> {
        val topics = zulipApi.getTopicsInStream(streamId).topics

        return concatWithUnreadMessages(topics, streamName)
    }

    private suspend fun concatWithUnreadMessages(
        topicsList: List<TopicModelNetwork>,
        streamName: String
    ) = topicsList.map {
        val unreadMessageCount = zulipApi.getTopicUnreadMessages(
            ZulipApi.buildMessagesNarrow(
                streamName,
                it.name
            )
        ).messages.size

        TopicModel(
            streamName = streamName,
            topicName = it.name,
            messagesUnread = unreadMessageCount
        )
    }
}