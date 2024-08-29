package com.viktorger.zulip_client.app.core.data.repository.fake

import com.viktorger.zulip_client.app.core.domain.repository.ProfileRepository
import com.viktorger.zulip_client.app.core.model.ProfileModel
import com.viktorger.zulip_client.app.core.model.UserStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeProfileRepository : ProfileRepository {
    override fun getProfileInfo(userId: Int): Flow<ProfileModel> = flow {
        emit(
            ProfileModel(
                userId = 1,
                username = "some username",
                status = UserStatus.Unknown,
                avatarUrl = null
            )
        )
    }

    override fun getOwnProfileInfo(): Flow<ProfileModel> = flow {
        emit(
            ProfileModel(
                userId = 0,
                username = "",
                status = UserStatus.Unknown,
                avatarUrl = null
            )
        )
    }
    override suspend fun getOwnProfileUserId(): Int = 0
}