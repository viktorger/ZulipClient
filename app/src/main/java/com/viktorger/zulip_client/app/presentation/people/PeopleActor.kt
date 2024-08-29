package com.viktorger.zulip_client.app.presentation.people

import com.viktorger.zulip_client.app.core.domain.repository.UserRepository
import com.viktorger.zulip_client.app.core.model.UserShortcutModel
import com.viktorger.zulip_client.app.core.model.UserStatus
import com.viktorger.zulip_client.app.presentation.mvi.MviActor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class PeopleActor @Inject constructor(
    private val userRepository: UserRepository
) : MviActor<PeoplePartialState, PeopleIntent, PeopleState, PeopleEffect>() {
    override fun resolve(intent: PeopleIntent, state: PeopleState): Flow<PeoplePartialState> =
        when (intent) {
            is PeopleIntent.LoadUsersWithQuery -> loadWithQuery(intent.query)
            PeopleIntent.UpdateData -> updateData()
            is PeopleIntent.NavigateToForeignProfile -> navigateToForeignProfile(intent.userId)
            PeopleIntent.LoadStatuses -> loadStatuses()
        }.flowOn(Dispatchers.Default)

    private fun loadStatuses(): Flow<PeoplePartialState> = userRepository.loadStatus()
        .mapStatusesToPartialState()
        .catch {
            _effects.emit(PeopleEffect.StatusesError)
        }

    private fun navigateToForeignProfile(userId: Int): Flow<PeoplePartialState> = flow {
        _effects.emit(
            PeopleEffect.NavigateToForeignProfile(userId)
        )
    }

    private fun updateData(): Flow<PeoplePartialState> = userRepository.updateAllUsers()
        .mapUsersToPartialState()
        .catch {
            _effects.emit(PeopleEffect.ShowError(it))
            emit(PeoplePartialState.Error(it))
        }.onCompletion {
            if (it == null) {
                loadStatuses().collect { partialState ->
                    emit(partialState)
                }
            }
        }

    private var usersLoadJob: Job? = null

    private fun loadWithQuery(
        query: String
    ): Flow<PeoplePartialState> = channelFlow<PeoplePartialState> {
        usersLoadJob?.cancel()
        usersLoadJob = userRepository.getAllUsers(query)
            .mapUsersToPartialState()
            .onEach { send(it) }
            .launchIn(this)

    }
        .onStart { emit(PeoplePartialState.PeopleLoading) }

    private fun Flow<List<UserShortcutModel>>.mapUsersToPartialState(): Flow<PeoplePartialState> =
        map { PeoplePartialState.UsersLoaded(it) }

    private fun Flow<Map<Int, UserStatus>>.mapStatusesToPartialState(): Flow<PeoplePartialState> =
        map { PeoplePartialState.StatusesLoaded(it) }
}
