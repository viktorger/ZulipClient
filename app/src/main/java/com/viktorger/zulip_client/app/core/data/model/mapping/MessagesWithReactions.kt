package com.viktorger.zulip_client.app.core.data.model.mapping

import com.viktorger.zulip_client.app.core.database.relationship.MessageWithReactions
import com.viktorger.zulip_client.app.core.model.MessageModel
import com.viktorger.zulip_client.app.core.model.ReactionWithCounter

internal fun messagesWithReactionsToExternal(
    messages: List<MessageWithReactions>
): List<MessageModel> = messages.map { it.toExternalModel() }

private fun MessageWithReactions.toExternalModel() = MessageModel(
    messageId = message.id,
    userId = message.userId,
    username = message.username,
    timestamp = message.timestamp,
    content = message.content,
    avatarUrl = message.avatarUrl,
    reactionsWithCounter = reactions.map { reaction ->
        ReactionWithCounter(
            emoji = reaction.emoji,
            count = reaction.count,
            isSelected = reaction.isSelected
        )
    }
)