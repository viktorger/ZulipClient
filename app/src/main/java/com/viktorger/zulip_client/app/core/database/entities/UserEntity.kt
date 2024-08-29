package com.viktorger.zulip_client.app.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey @ColumnInfo(name = "user_id") val userId: Int,
    val username: String,
    val email: String,
    @ColumnInfo("avatar_url") val avatarUrl: String?,

)