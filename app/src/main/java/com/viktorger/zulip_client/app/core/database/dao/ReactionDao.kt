package com.viktorger.zulip_client.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.viktorger.zulip_client.app.core.database.entities.ReactionEntity

@Dao
interface ReactionDao {
    @Insert
    fun insertReactions(reactions: List<ReactionEntity>)

    @Query("DELETE FROM reaction")
    fun deleteAll()

    @Query("DELETE FROM reaction WHERE message_id IN (:messagesIds)")
    fun deleteReactionsByMessagesIds(messagesIds: List<Int>)

    @Transaction
    open fun updateReactions(reactions: List<ReactionEntity>) {
        deleteAll()
        insertReactions(reactions)
    }
}