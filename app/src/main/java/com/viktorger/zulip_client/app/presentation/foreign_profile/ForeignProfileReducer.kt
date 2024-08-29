package com.viktorger.zulip_client.app.presentation.foreign_profile

import com.viktorger.zulip_client.app.core.ui.model.LceState
import com.viktorger.zulip_client.app.core.ui.model.mapping.toUi
import com.viktorger.zulip_client.app.presentation.mvi.MviReducer

class ForeignProfileReducer : MviReducer<
        ForeignProfileState,
        ForeignProfilePartialState> {
    override fun reduce(prevState: ForeignProfileState, partialState: ForeignProfilePartialState): ForeignProfileState =
        when (partialState) {
            is ForeignProfilePartialState.ProfileLoaded -> {
                prevState.copy(
                    profileUi = LceState.Content(partialState.profileModel.toUi())
                )
            }

            is ForeignProfilePartialState.Error -> {
                if (prevState.profileUi is LceState.Content) {
                    prevState
                } else {
                    prevState.copy(
                        profileUi = LceState.Error(partialState.error)
                    )
                }
            }

            ForeignProfilePartialState.ProfileLoading -> {
                prevState.copy(
                    profileUi = LceState.Loading
                )
            }
        }
}
