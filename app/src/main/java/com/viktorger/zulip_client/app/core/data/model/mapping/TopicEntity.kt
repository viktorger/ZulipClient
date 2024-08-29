package com.viktorger.zulip_client.app.core.data.model.mapping

import com.viktorger.zulip_client.app.core.database.entities.TopicEntity
import com.viktorger.zulip_client.app.core.model.TopicModel

internal fun topicEntitiesToExternal(
    topics: List<TopicEntity>
): List<TopicModel> = topics.map { it.toExternalModel() }

private fun TopicEntity.toExternalModel() = TopicModel(
    streamName = streamName,
    topicName = name,
    messagesUnread = messagesCount
)

