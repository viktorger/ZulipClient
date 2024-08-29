package com.viktorger.zulip_client.app.core.data.model.mapping

import com.viktorger.zulip_client.app.core.database.entities.TopicEntity
import com.viktorger.zulip_client.app.core.model.TopicModel

internal fun topicModelsToEntities(
    topics: List<TopicModel>
): List<TopicEntity> = topics.map { it.toEntity() }

private fun TopicModel.toEntity() = TopicEntity(
    name = topicName,
    streamName = streamName,
    messagesCount = messagesUnread
)