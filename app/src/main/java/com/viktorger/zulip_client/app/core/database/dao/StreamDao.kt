package com.viktorger.zulip_client.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.viktorger.zulip_client.app.core.database.entities.StreamEntity

@Dao
interface StreamDao {

    @Query("SELECT name FROM stream")
    fun getAllStreamsNames(): List<String>
    @Query("SELECT name FROM stream WHERE is_subscribed=1")
    fun getSubscribedStreamsNames(): List<String>

    @Query("SELECT * FROM stream WHERE is_subscribed=1 AND name LIKE :query ORDER BY name")
    fun getSubscribedStreams(query: String): List<StreamEntity>

    @Query("SELECT * FROM stream WHERE name LIKE :query ORDER BY name")
    fun getAllStreams(query: String): List<StreamEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStream(streamEntity: StreamEntity): Long

    @Query("UPDATE stream SET is_subscribed=0")
    fun setAllStreamsUnsubscribed()

    @Query("DELETE FROM stream WHERE name in (:streamsNames)")
    fun deleteByNames(streamsNames: List<String>)

    @Update
    fun updateStream(streamEntity: StreamEntity)

    @Transaction
    open fun rewriteAllStreams(streamEntities: List<StreamEntity>) {
        val subscribedNames = getSubscribedStreamsNames()
        val mappedStreamEntities = streamEntities.map {
            if (it.name in subscribedNames) {
                it.copy(isSubscribed = true)
            } else {
                it
            }
        }

        val oldNames = getAllStreamsNames()
        val newNames = streamEntities.map { it.name }
        val toDelete = oldNames.filter { it !in newNames }
        deleteByNames(toDelete)
        mappedStreamEntities.forEach { insertOrUpdate(it) }
    }

    @Transaction
    open fun rewriteSubscribedStreams(streamEntities: List<StreamEntity>) {
        setAllStreamsUnsubscribed()
        streamEntities.forEach { insertOrUpdate(it) }
    }

    @Transaction
    open fun insertOrUpdate(streamEntity: StreamEntity) {
        if (insertStream(streamEntity) == -1L) {
            updateStream(streamEntity)
        }
    }
}