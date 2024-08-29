package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class TopicList(
    val topics: List<TopicModelNetwork>
)