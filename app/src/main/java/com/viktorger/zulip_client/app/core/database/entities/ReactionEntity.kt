package com.viktorger.zulip_client.app.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "reaction",
    foreignKeys = [
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["message_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["emoji", "message_id"]
)
data class ReactionEntity(
    val emoji: String,
    @ColumnInfo(name = "message_id") val messageId: Int,
    val count: Int,
    @ColumnInfo(name = "is_selected") val isSelected: Boolean
)
