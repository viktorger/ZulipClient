package com.viktorger.zulip_client.app.presentation.people

import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.core.ui.model.UserStatusUi
import com.viktorger.zulip_client.app.core.ui.model.mapping.toUi
import com.viktorger.zulip_client.app.core.ui.model.mapping.userShortcutModelsToUi
import com.viktorger.zulip_client.app.presentation.mvi.MviReducer

class PeopleReducer : MviReducer<PeopleState, PeoplePartialState> {
    override fun reduce(prevState: PeopleState, partialState: PeoplePartialState): PeopleState {
        return when (partialState) {
            is PeoplePartialState.UsersLoaded -> usersLoaded(prevState, partialState)

            is PeoplePartialState.Error -> {
                usersError(prevState, partialState)
            }

            PeoplePartialState.PeopleLoading -> peopleLoading(prevState)
            is PeoplePartialState.StatusesLoaded -> statusesLoaded(prevState, partialState)
        }
    }

    private fun statusesLoaded(
        prevState: PeopleState,
        partialState: PeoplePartialState.StatusesLoaded
    ): PeopleState {
        if (prevState.peopleUi !is LceState.Content) {
            return prevState
        }
        val statusMap = partialState.statuses.mapValues { it.value.toUi() }
        val users = prevState.peopleUi.data
        val newUsers = users.map { it.copy(
            status = statusMap[it.userId] ?: UserStatusUi.Unknown
        ) }
        return prevState.copy(
            peopleUi = LceState.Content(newUsers),
            statusMap = statusMap
        )
    }

    private fun peopleLoading(prevState: PeopleState) =
        if (prevState.peopleUi is LceState.Content) {
            prevState
        } else {
            prevState.copy(
                peopleUi = LceState.Loading
            )
        }

    private fun usersError(
        prevState: PeopleState,
        partialState: PeoplePartialState.Error
    ) = if (prevState.peopleUi is LceState.Content) {
        prevState
    } else {
        prevState.copy(
            peopleUi = LceState.Error(partialState.error)
        )
    }

    private fun usersLoaded(
        prevState: PeopleState,
        partialState: PeoplePartialState.UsersLoaded
    ): PeopleState {
        val users = userShortcutModelsToUi(partialState.userShortcutModel)
        val statusMap = prevState.statusMap
        val newUsers = users.map { it.copy(
            status = statusMap[it.userId] ?: UserStatusUi.Unknown
        ) }

        return prevState.copy(
            peopleUi = LceState.Content(newUsers)
        )
    }
}