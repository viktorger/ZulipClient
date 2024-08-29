package com.viktorger.zulip_client.app.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "message",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["name", "stream_name"],
            childColumns = ["topic_name", "stream_name"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class MessageEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    val username: String,
    val timestamp: Long,
    val content: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String?,

    @ColumnInfo(name = "topic_name") val topicName: String,
    @ColumnInfo(name = "stream_name") val streamName: String
)