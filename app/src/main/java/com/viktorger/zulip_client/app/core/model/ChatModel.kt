package com.viktorger.zulip_client.app.core.model


data class ChatModel (
    val receivedMessages: List<MessageReceivedModel>,
    val sentMessages: List<MessageSentModel>
)