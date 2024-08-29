package com.viktorger.zulip_client.app.presentation.people

import com.viktorger.zulip_client.app.core.model.UserShortcutModel
import com.viktorger.zulip_client.app.core.model.UserStatus
import com.viktorger.zulip_client.app.presentation.mvi.MviPartialState

sealed interface PeoplePartialState : MviPartialState {
    data class UsersLoaded(val userShortcutModel: List<UserShortcutModel>) : PeoplePartialState
    data object PeopleLoading : PeoplePartialState
    class Error(val error: Throwable) : PeoplePartialState
    data class StatusesLoaded(val statuses: Map<Int, UserStatus>): PeoplePartialState
}