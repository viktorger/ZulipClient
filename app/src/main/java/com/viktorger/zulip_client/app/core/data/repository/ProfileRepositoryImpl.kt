package com.viktorger.zulip_client.app.core.data.repository

import com.viktorger.zulip_client.app.core.common.USER_ID_NOT_FOUND
import com.viktorger.zulip_client.app.core.common.getUserStatus
import com.viktorger.zulip_client.app.core.domain.repository.ProfileRepository
import com.viktorger.zulip_client.app.core.model.ProfileModel
import com.viktorger.zulip_client.app.core.network.ZulipApi
import com.viktorger.zulip_client.app.core.shared_preferences.SharedPreferencesDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val zulipApi: ZulipApi,
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
) : ProfileRepository {

    override fun getProfileInfo(userId: Int): Flow<ProfileModel> = flow {
        val user = withContext(Dispatchers.IO) {
            zulipApi.getProfile(userId).user
        }
        val status = zulipApi.getUserPresence(user.userId).presence.aggregated.status
        emit(
            ProfileModel(
                username = user.fullName,
                status = getUserStatus(status),
                avatarUrl = user.avatarUrl,
                userId = userId
            )
        )
    }

    override fun getOwnProfileInfo(): Flow<ProfileModel> = flow {
        val user = withContext(Dispatchers.IO) {
            zulipApi.getOwnProfile()
        }
        val status = zulipApi.getUserPresence(user.userId).presence.aggregated.status
        emit(
            ProfileModel(
                username = user.fullName,
                status = getUserStatus(status),
                avatarUrl = user.avatarUrl,
                userId = user.userId
            )
        )
    }

    override suspend fun getOwnProfileUserId(): Int {
        var userId = sharedPreferencesDataSource.getUserId()
        if (userId == USER_ID_NOT_FOUND) {
            userId = zulipApi.getOwnProfile().userId
            sharedPreferencesDataSource.saveUserId(userId)
        }

        return userId
    }
}