package com.viktorger.zulip_client.app.core.domain.repository

import com.viktorger.zulip_client.app.core.model.ProfileModel
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfileInfo(userId: Int): Flow<ProfileModel>
    fun getOwnProfileInfo(): Flow<ProfileModel>
    suspend fun getOwnProfileUserId(): Int
}