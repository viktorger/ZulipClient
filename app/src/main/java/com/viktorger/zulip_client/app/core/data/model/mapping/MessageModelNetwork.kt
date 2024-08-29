package com.viktorger.zulip_client.app.core.data.model.mapping

import com.viktorger.zulip_client.app.core.common.textFromHtml
import com.viktorger.zulip_client.app.core.common.toMillis
import com.viktorger.zulip_client.app.core.model.MessageModel
import com.viktorger.zulip_client.app.core.network.model.MessageModelNetwork

internal fun messageModelsNetworkToExternal(
    messages: List<MessageModelNetwork>,
    userId: Int
): List<MessageModel> = messages.map {
    it.toExternalModel(userId)
}

fun MessageModelNetwork.toExternalModel(userId: Int) = MessageModel(
    messageId = messageId,
    userId = this.userId,
    username = username,
    timestamp = toMillis(timestamp),
    content = textFromHtml(content),
    avatarUrl = avatarUrl,
    reactionsWithCounter = reactionModelsNetworkToExternal(reactions, userId),
)