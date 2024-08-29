package com.viktorger.zulip_client.app.core.database.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.viktorger.zulip_client.app.core.database.entities.MessageEntity
import com.viktorger.zulip_client.app.core.database.entities.ReactionEntity


data class MessageWithReactions(
    @Embedded val message: MessageEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "message_id"
    )
    val reactions: List<ReactionEntity>
)
