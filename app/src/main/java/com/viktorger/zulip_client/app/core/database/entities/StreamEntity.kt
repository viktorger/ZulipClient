package com.viktorger.zulip_client.app.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stream")
data class StreamEntity(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "is_subscribed") val isSubscribed: Boolean
)
