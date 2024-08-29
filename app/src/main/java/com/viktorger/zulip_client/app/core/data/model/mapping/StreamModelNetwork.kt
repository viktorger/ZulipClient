package com.viktorger.zulip_client.app.core.data.model.mapping

import com.viktorger.zulip_client.app.core.database.entities.StreamEntity
import com.viktorger.zulip_client.app.core.model.StreamModel
import com.viktorger.zulip_client.app.core.network.model.StreamModelNetwork

internal fun streamModelsNetworkToEntities(
    streams: List<StreamModelNetwork>,
    isSubscribed: Boolean
): List<StreamEntity> = streams.map { it.toEntity(isSubscribed) }

private fun StreamModelNetwork.toEntity(isSubscribed: Boolean) = StreamEntity(
    name = name,
    isSubscribed = isSubscribed
)

internal fun streamModelsNetworkToExternal(
    streams: List<StreamModelNetwork>,
) = streams.map { it.toExternal() }

private fun StreamModelNetwork.toExternal() = StreamModel(name)
