package com.viktorger.zulip_client.app.core.data.model.mapping

import com.viktorger.zulip_client.app.core.common.EVENT_TYPE_MESSAGE
import com.viktorger.zulip_client.app.core.common.EVENT_TYPE_REACTION
import com.viktorger.zulip_client.app.core.common.REACTION_ADD
import com.viktorger.zulip_client.app.core.common.REACTION_TYPE_UNICODE
import com.viktorger.zulip_client.app.core.common.getEmojiByUnicode
import com.viktorger.zulip_client.app.core.model.EventModel
import com.viktorger.zulip_client.app.core.model.ReactionChangeModel
import com.viktorger.zulip_client.app.core.network.model.EventNetwork
import com.viktorger.zulip_client.app.core.network.model.MessageModelNetwork

internal fun networkEventsToExternal(
    events: List<EventNetwork>,
    userId: Int
) = events.map { it.toExternalModel(userId) }


private fun EventNetwork.toExternalModel(userId: Int) = when {
    this.type == EVENT_TYPE_MESSAGE -> {
        eventModelMessage(this.eventId, this.message!!, userId)
    }

    this.type == EVENT_TYPE_REACTION &&
            this.reactionType == REACTION_TYPE_UNICODE -> {
        if (this.op == REACTION_ADD) {
            eventModelReaction(
                eventId = this.eventId,
                messageId = this.messageId!!,
                emojiCode = this.emojiCode!!,
                isOwnUser = this.userId == userId,
                isAdded = true
            )
        } else {
            eventModelReaction(
                eventId = this.eventId,
                messageId = this.messageId!!,
                emojiCode = this.emojiCode!!,
                isOwnUser = this.userId == userId,
                isAdded = false
            )
        }
    }

    else -> EventModel(this.eventId)
}

private fun eventModelReaction(
    eventId: Int,
    messageId: Int,
    emojiCode: String,
    isOwnUser: Boolean,
    isAdded: Boolean
) = EventModel(
    eventId = eventId,
    reaction = ReactionChangeModel(
        messageId = messageId,
        emoji = getEmojiByUnicode(emojiCode),
        isOwnUser = isOwnUser,
        isAdded = isAdded
    )
)

private fun eventModelMessage(
    eventId: Int,
    message: MessageModelNetwork,
    userId: Int
): EventModel = EventModel(
    eventId = eventId,
    message = message.toExternalModel(userId)
)