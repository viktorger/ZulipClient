package com.viktorger.zulip_client.app.presentation.profile

import com.viktorger.zulip_client.app.core.domain.repository.ProfileRepository
import com.viktorger.zulip_client.app.core.model.ProfileModel
import com.viktorger.zulip_client.app.presentation.mvi.MviActor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ProfileActor @Inject constructor(
    private val profileRepository: ProfileRepository
) : MviActor<
        ProfilePartialState,
        ProfileIntent,
        ProfileState,
        ProfileEffect>() {
    override fun resolve(intent: ProfileIntent, state: ProfileState): Flow<ProfilePartialState> =
        when (intent) {
            ProfileIntent.Init -> {
                loadProfile()
            }
        }.flowOn(Dispatchers.Default)

    private fun loadProfile(): Flow<ProfilePartialState> =
        profileRepository.getOwnProfileInfo()
            .mapToPartialState()
            .catch {
                _effects.emit(ProfileEffect.ShowError(it))
                emit(ProfilePartialState.Error(it))
            }
            .onStart { emit(ProfilePartialState.ProfileLoading) }

    private fun Flow<ProfileModel>.mapToPartialState(): Flow<ProfilePartialState> =
        map { ProfilePartialState.ProfileLoaded(it) }
}