package com.viktorger.zulip_client.app.presentation.people

import com.viktorger.zulip_client.app.presentation.mvi.MviIntent

sealed interface PeopleIntent : MviIntent {
    data class LoadUsersWithQuery(val query: String) : PeopleIntent
    data class NavigateToForeignProfile(val userId: Int) : PeopleIntent
    data object UpdateData : PeopleIntent
    data object LoadStatuses: PeopleIntent
}