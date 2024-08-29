package com.viktorger.zulip_client.app.core.model

data class EventModel(
    val eventId: Int,
    val reaction: ReactionChangeModel? = null,
    val message: MessageModel? = null
)
