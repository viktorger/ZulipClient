package com.viktorger.zulip_client.app.presentation.people

import com.viktorger.zulip_client.app.presentation.mvi.MviEffect

sealed interface PeopleEffect : MviEffect {
    data object StatusesError : PeopleEffect
    data class ShowError(val throwable: Throwable) : PeopleEffect
    data class NavigateToForeignProfile(val userId: Int) : PeopleEffect
}
