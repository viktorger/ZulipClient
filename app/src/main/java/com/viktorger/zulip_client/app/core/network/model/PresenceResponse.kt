package com.viktorger.zulip_client.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PresenceResponse(
    val presence: PresenceModelNetwork
)

@Serializable
data class PresenceModelNetwork(
    val aggregated: AggregatedModelNetwork
)

@Serializable
data class AggregatedModelNetwork(
    val status: String
)
