package com.viktorger.zulip_client.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.viktorger.zulip_client.app.core.database.dao.MessageDao
import com.viktorger.zulip_client.app.core.database.dao.ReactionDao
import com.viktorger.zulip_client.app.core.database.dao.StreamDao
import com.viktorger.zulip_client.app.core.database.dao.TopicDao
import com.viktorger.zulip_client.app.core.database.dao.UserDao
import com.viktorger.zulip_client.app.core.database.entities.MessageEntity
import com.viktorger.zulip_client.app.core.database.entities.ReactionEntity
import com.viktorger.zulip_client.app.core.database.entities.StreamEntity
import com.viktorger.zulip_client.app.core.database.entities.TopicEntity
import com.viktorger.zulip_client.app.core.database.entities.UserEntity

@Database(
    entities = [
        StreamEntity::class,
        TopicEntity::class,
        MessageEntity::class,
        ReactionEntity::class,
        UserEntity::class
    ],
    version = 1
)
abstract class MessengerDatabase : RoomDatabase() {
    abstract fun streamDao(): StreamDao
    abstract fun topicDao(): TopicDao
    abstract fun messageDao(): MessageDao
    abstract fun reactionDao(): ReactionDao
    abstract fun userDao(): UserDao
}