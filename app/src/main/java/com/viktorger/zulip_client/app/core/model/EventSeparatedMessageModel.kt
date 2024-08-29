package com.viktorger.zulip_client.app.core.model

data class EventSeparatedMessageModel(
    val eventId: Int,
    val reaction: ReactionChangeModel? = null,
    val sentMessage: MessageSentModel? = null,
    val receivedMessage: MessageReceivedModel? = null
)
