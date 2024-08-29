package com.viktorger.zulip_client.app.core.data.repository

import com.viktorger.zulip_client.app.core.common.buildSqlLikeQuery
import com.viktorger.zulip_client.app.core.common.getUserStatus
import com.viktorger.zulip_client.app.core.data.model.mapping.toExternalModel
import com.viktorger.zulip_client.app.core.data.model.mapping.userEntitiesToExternal
import com.viktorger.zulip_client.app.core.data.model.mapping.userShortcutModelsNetworkToEntities
import com.viktorger.zulip_client.app.core.database.dao.UserDao
import com.viktorger.zulip_client.app.core.domain.repository.UserRepository
import com.viktorger.zulip_client.app.core.model.UserShortcutModel
import com.viktorger.zulip_client.app.core.model.UserStatus
import com.viktorger.zulip_client.app.core.network.ZulipApi
import com.viktorger.zulip_client.app.core.network.model.UserShortcutModelNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val zulipApi: ZulipApi
) : UserRepository {
    override fun getAllUsers(query: String): Flow<List<UserShortcutModel>> = flow {
        val cached = userDao.getAllUsers(buildSqlLikeQuery(query))
        if (!(cached.isEmpty() && query.isEmpty())) {
            emit(userEntitiesToExternal(cached))
        }
    }.flowOn(Dispatchers.IO)

    override fun updateAllUsers(): Flow<List<UserShortcutModel>> = flow {
        val users = zulipApi.getAllUsers().members
        val filteredSortedUsers = users.filter {
            !it.isBot && it.isActive
        }.sortedBy { it.fullName }
        emit(userShortcutModelsNetworkToExternal(filteredSortedUsers))

        updateCachedUsers(filteredSortedUsers)
    }.flowOn(Dispatchers.IO)

    override fun loadStatus(): Flow<Map<Int, UserStatus>> = flow {
        val users = zulipApi.getAllUsers().members
        val filteredSortedUsers = users.filter {
            !it.isBot && it.isActive
        }.sortedBy { it.fullName }

        val map = mutableMapOf<Int, UserStatus>()
        filteredSortedUsers.forEach {
            val status = zulipApi.getUserPresence(it.userId).presence.aggregated.status
            val userStatus = getUserStatus(status)

            map[it.userId] = userStatus
        }
        emit(map)
    }

    private fun userShortcutModelsNetworkToExternal(
        users: List<UserShortcutModelNetwork>
    ): List<UserShortcutModel> = users.map { it.toExternalModel() }

    private fun updateCachedUsers(users: List<UserShortcutModelNetwork>) {
        val userEntities = userShortcutModelsNetworkToEntities(users)
        userDao.rewriteUsers(userEntities)
    }
}


