package com.viktorger.zulip_client.app.core.domain.repository

import com.viktorger.zulip_client.app.core.model.UserShortcutModel
import com.viktorger.zulip_client.app.core.model.UserStatus
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAllUsers(query: String): Flow<List<UserShortcutModel>>
    fun updateAllUsers(): Flow<List<UserShortcutModel>>
    fun loadStatus(): Flow<Map<Int, UserStatus>>
}