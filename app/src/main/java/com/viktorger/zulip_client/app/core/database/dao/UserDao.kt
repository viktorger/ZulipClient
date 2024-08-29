package com.viktorger.zulip_client.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.viktorger.zulip_client.app.core.database.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE username LIKE :query ORDER BY username")
    fun getAllUsers(query: String): List<UserEntity>

    @Insert
    fun insertUsers(users: List<UserEntity>)

    @Query("DELETE FROM user")
    fun deleteAll()

    @Transaction
    open fun rewriteUsers(users: List<UserEntity>) {
        deleteAll()
        insertUsers(users)
    }
}