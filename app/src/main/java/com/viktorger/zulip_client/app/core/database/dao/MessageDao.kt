package com.viktorger.zulip_client.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.viktorger.zulip_client.app.core.database.entities.MessageEntity
import com.viktorger.zulip_client.app.core.database.relationship.MessageWithReactions

@Dao
interface MessageDao {
    @Transaction
    @Query(
        """
        SELECT * FROM message WHERE stream_name=:streamName AND topic_name=:topicName
            ORDER BY id ASC
        """
    )
    fun getMessagesWithReaction(streamName: String, topicName: String): List<MessageWithReactions>

    @Query("SELECT count(*) FROM message WHERE stream_name=:streamName AND topic_name=:topicName")
    fun getMessagesCount(streamName: String, topicName: String): Int

    @Query("SELECT max(id) FROM message WHERE stream_name=:streamName AND topic_name=:topicName")
    fun getMaxMessageIdInChat(streamName: String, topicName: String): Int

    @Query("SELECT min(id) FROM message WHERE stream_name=:streamName AND topic_name=:topicName")
    fun getMinMessageIdInChat(streamName: String, topicName: String): Int

    @Query(
        """
        SELECT id FROM message WHERE stream_name=:streamName AND topic_name=:topicName
            ORDER BY id ASC
        """
    )
    fun getMessagesIds(streamName: String, topicName: String): List<Int>

    @Query("DELETE FROM message WHERE id IN (:ids)")
    fun deleteMessagesByIds(ids: List<Int>)

    @Insert
    fun insertMessages(messages: List<MessageEntity>)

    @Query("DELETE FROM message WHERE stream_name=:streamName AND topic_name=:topicName")
    fun clearChat(streamName: String, topicName: String)

    @Transaction
    open fun updateMessages(
        messages: List<MessageEntity>,
        streamName: String,
        topicName: String
    ) {
        clearChat(streamName, topicName)
        insertMessages(messages)
    }
}