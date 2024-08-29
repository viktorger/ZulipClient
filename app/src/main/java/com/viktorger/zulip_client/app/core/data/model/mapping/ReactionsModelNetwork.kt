package com.viktorger.zulip_client.app.core.data.model.mapping

import com.viktorger.zulip_client.app.core.common.REACTION_TYPE_UNICODE
import com.viktorger.zulip_client.app.core.common.getEmojiByUnicode
import com.viktorger.zulip_client.app.core.database.entities.ReactionEntity
import com.viktorger.zulip_client.app.core.model.ReactionWithCounter
import com.viktorger.zulip_client.app.core.network.model.ReactionModelNetwork

internal fun reactionModelsNetworkToExternal(
    reactions: List<ReactionModelNetwork>,
    userId: Int
): List<ReactionWithCounter> {
    val reactionsWithCounter: MutableList<ReactionWithCounter> = mutableListOf()
    reactions.forEach {
        if (it.reactionType != REACTION_TYPE_UNICODE) {
            return@forEach
        }
        val position = reactionsWithCounter.indexOfFirst { reactionWithCounter ->
            reactionWithCounter.emoji == it.emojiCode
        }

        if (position == -1) {
            reactionsWithCounter.add(createReactionWithCounter(it, userId))
        } else {
            val reactionWithCounter = reactionsWithCounter[position]
            reactionsWithCounter[position] = reactionWithCounter.copy(
                count = reactionWithCounter.count + 1,
                isSelected = reactionWithCounter.isSelected || it.userId == userId
            )
        }
    }
    return reactionsWithCounter.map {
        it.copy(
            emoji = getEmojiByUnicode(it.emoji)
        )
    }
}

private fun createReactionWithCounter(
    reactionModelNetwork: ReactionModelNetwork,
    userId: Int
): ReactionWithCounter = ReactionWithCounter(
    emoji = reactionModelNetwork.emojiCode,
    count = 1,
    isSelected = reactionModelNetwork.userId == userId
)

internal fun reactionModelsNetworkToEntities(
    reactions: List<ReactionModelNetwork>,
    messageId: Int,
    userId: Int
): List<ReactionEntity> {
    val reactionsWithCounter: MutableList<ReactionEntity> = mutableListOf()
    reactions.forEach {
        if (it.reactionType != REACTION_TYPE_UNICODE) {
            return@forEach
        }
        val position = reactionsWithCounter.indexOfFirst { reactionWithCounter ->
            reactionWithCounter.emoji == it.emojiCode
        }
        if (position == -1) {
            reactionsWithCounter.add(createReactionEntity(it, messageId, userId))
        } else {
            val reactionWithCounter = reactionsWithCounter[position]
            reactionsWithCounter[position] = reactionWithCounter.copy(
                count = reactionWithCounter.count + 1,
                isSelected = reactionWithCounter.isSelected || (it.userId == userId)
            )
        }
    }
    return reactionsWithCounter.map {
        it.copy(
            emoji = getEmojiByUnicode(it.emoji)
        )
    }
}

private fun createReactionEntity(
    reactionModelNetwork: ReactionModelNetwork,
    messageId: Int,
    userId: Int
): ReactionEntity = ReactionEntity(
    emoji = reactionModelNetwork.emojiCode,
    count = 1,
    isSelected = reactionModelNetwork.userId == userId,
    messageId = messageId
)