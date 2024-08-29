package com.viktorger.zulip_client.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.viktorger.zulip_client.app.core.database.entities.TopicEntity

@Dao
interface TopicDao {
    @Query("SELECT name FROM topic WHERE stream_name=:streamName")
    fun getTopicsNamesByStreamName(streamName: String): List<String>

    @Query("SELECT * FROM topic WHERE stream_name=:streamName ORDER BY name")
    fun getTopicsByStreamName(streamName: String): List<TopicEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTopic(topicEntity: TopicEntity): Long

    @Update
    fun updateTopic(topicEntity: TopicEntity)

    @Query("DELETE FROM topic WHERE stream_name=:streamName AND name in (:topicNames)")
    fun deleteByStreamNameAndTopicsNames(streamName: String, topicNames: List<String>)

    @Transaction
    open fun updateTopicsInStream(streamName: String, topicEntities: List<TopicEntity>) {
        val oldNames = getTopicsNamesByStreamName(streamName)
        val newNames = topicEntities.map { it.name }
        val toDelete = oldNames.filter { it !in newNames }
        deleteByStreamNameAndTopicsNames(streamName, toDelete)

        topicEntities.forEach { insertOrUpdate(it) }
    }

    @Transaction
    fun insertOrUpdate(topicEntity: TopicEntity) {
        if (insertTopic(topicEntity) == -1L) {
            updateTopic(topicEntity)
        }
    }
}