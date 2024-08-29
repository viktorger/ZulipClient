package com.viktorger.zulip_client.app.core.model

data class MessageModel(
    val messageId: Int,
    val userId: Int,
    val username: String,
    val timestamp: Long,
    val content: String,
    val avatarUrl: String?,
    val reactionsWithCounter: List<ReactionWithCounter>
) {
    fun toReceivedModel() = MessageReceivedModel(
        messageId = messageId,
        userId = userId,
        username = username,
        timestamp = timestamp,
        content = content,
        avatarUrl = avatarUrl,
        reactionsWithCounter = reactionsWithCounter
    )

    fun toSentModel() = MessageSentModel(
        messageId = messageId,
        timestamp = timestamp,
        content = content,
        reactionsWithCounter = reactionsWithCounter
    )
}

internal fun chatModelFromMessageModels(
    messages: List<MessageModel>,
    currentUserId: Int
): ChatModel = ChatModel(
    receivedMessages = messageModelsToReceived(messages.filter { it.userId != currentUserId }),
    sentMessages = messageModelsToSent(messages.filter { it.userId == currentUserId })
)

private fun messageModelsToReceived(
    messages: List<MessageModel>
): List<MessageReceivedModel> = messages.map { it.toReceivedModel() }

private fun messageModelsToSent(
    messages: List<MessageModel>
): List<MessageSentModel> = messages.map { it.toSentModel() }