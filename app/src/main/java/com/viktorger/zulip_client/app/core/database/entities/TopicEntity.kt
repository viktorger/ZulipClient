package com.viktorger.zulip_client.app.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "topic",
    foreignKeys = [
        ForeignKey(
            entity = StreamEntity::class,
            parentColumns = ["name"],
            childColumns = ["stream_name"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["name", "stream_name"]
)
data class TopicEntity(
    val name: String,
    @ColumnInfo(name = "stream_name") val streamName: String,
    @ColumnInfo(name = "messages_count") val messagesCount: Int
)