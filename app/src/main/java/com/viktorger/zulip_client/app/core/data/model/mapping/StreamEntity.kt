package com.viktorger.zulip_client.app.core.data.model.mapping

import com.viktorger.zulip_client.app.core.database.entities.StreamEntity
import com.viktorger.zulip_client.app.core.model.StreamModel

private fun StreamEntity.toExternalModel(): StreamModel = StreamModel(name)

internal fun streamEntitiesToExternal(
    streams: List<StreamEntity>
): List<StreamModel> = streams.map { it.toExternalModel() }