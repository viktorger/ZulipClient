package com.viktorger.zulip_client.app.presentation.foreign_profile

import com.viktorger.zulip_client.app.core.domain.repository.ProfileRepository
import com.viktorger.zulip_client.app.core.model.ProfileModel
import com.viktorger.zulip_client.app.presentation.mvi.MviActor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ForeignProfileActor @Inject constructor(
    private val profileRepository: ProfileRepository
) : MviActor<
        ForeignProfilePartialState,
        ForeignProfileIntent,
        ForeignProfileState,
        ForeignProfileEffect>() {
    override fun resolve(intent: ForeignProfileIntent, state: ForeignProfileState): Flow<ForeignProfilePartialState> =
        when (intent) {
            is ForeignProfileIntent.LoadProfile -> {
                loadProfile(intent.userId)
            }

            ForeignProfileIntent.NavigateBack -> navigateBack()
        }.flowOn(Dispatchers.Default)

    private fun navigateBack(): Flow<ForeignProfilePartialState> = flow {
        _effects.emit(ForeignProfileEffect.NavigateBack)
    }

    private fun loadProfile(userId: Int): Flow<ForeignProfilePartialState> =
        profileRepository.getProfileInfo(userId)
            .mapToPartialState()
            .catch {
                _effects.emit(ForeignProfileEffect.ShowError(it))
                emit(ForeignProfilePartialState.Error(it))
            }
            .onStart { emit(ForeignProfilePartialState.ProfileLoading) }

    private fun Flow<ProfileModel>.mapToPartialState(): Flow<ForeignProfilePartialState> =
        map { ForeignProfilePartialState.ProfileLoaded(it) }
}